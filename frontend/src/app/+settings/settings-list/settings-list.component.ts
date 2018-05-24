import {Component, OnDestroy, OnInit} from '@angular/core';
import {IndexService} from '../../+indices/index.service';
import {AppConfiguation, SettingsService} from '../settings.service';
import {Subscription} from 'rxjs/Rx';
import {Router} from '@angular/router';

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

  isReloading = false;
  saveSuccess = false;

  error = null;

  constructor(private indexService: IndexService, private settingsService: SettingsService) {
  }

  ngOnInit() {
    this.indexService.getActiveComponentIds().subscribe(
      ids => this.activeIndices = ids
    );
    this.settingsService.get().subscribe(cfg => this.config = cfg);
    this.status$ = this.settingsService.status().subscribe(status => this.status = status);
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

    this.settingsService.save(<AppConfiguation>this.config).subscribe(() => {
      this.saveSuccess = true;
      setTimeout( () => this.saveSuccess = false, 5000);

      // redirect if password was changed
      if (this.config['spring.security.user.password'] && this.config['spring.security.user.password'].length > 0) {
        this.isReloading = true;
        setTimeout( () => window.location.replace('./login'), 5000);
      }
    }, (error) => this.handleError(error));
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

  handleError(error: any) {
    console.error('Error happened: ', error);
    this.error = error.statusText || error.message || error.json().message;
  }
}
