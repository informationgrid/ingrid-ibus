import {Component, OnDestroy, OnInit} from '@angular/core';
import {IndexService} from '../../+indices/index.service';
import {AppConfiguation, SettingsService} from '../settings.service';
import {Subscription} from 'rxjs/Rx';

@Component({
  selector: 'app-settings-list',
  templateUrl: './settings-list.component.html',
  styleUrls: ['./settings-list.component.scss']
})
export class SettingsListComponent implements OnInit, OnDestroy {

  esUrls = ['url1', 'url2'];

  activeIndices: string[];

  config: AppConfiguation = {};

  isEmptyPassword = false;
  private status: any = {};
  private status$: Subscription;

  constructor(private indexService: IndexService, private settingsService: SettingsService) {
  }

  ngOnInit() {
    this.indexService.getActiveComponentIds().subscribe(
      ids => this.activeIndices = ids
    );
    this.settingsService.get().subscribe(cfg => this.config = cfg);
    this.status$ = this.settingsService.status().subscribe( status => this.status = status );
  }

  ngOnDestroy() {
    this.status$.unsubscribe();
  }

  saveSettings() {
    if (this.isEmptyPassword) {
      this.config['codelistrepo.password'] = '';
    } else if (this.config['codelistrepo.password'] === '') {
      delete this.config['codelistrepo.password'];
    }

    this.settingsService.save(<AppConfiguation>this.config).subscribe(status => {
      console.log('Status', status);
    });
  }

  handleEmptyPassword(event: Event) {
    this.isEmptyPassword = (<HTMLInputElement>event.target).checked;
  }

  getStatusClass(component: string) {
    let state = '';
    switch (component) {
      case 'codelistrepo':
        state = this.status['codelistrepo'] === 'true'
            ? 'connected' : 'disconnected';
    }
    return 'fa fa-circle ' + state;
  }

}
