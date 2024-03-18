/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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

  activeIndices: string[];

  config: AppConfiguation = {};

  isEmptyPassword = false;
  statusCodelistRepo = false;
  statusElasticsearch = false;
  private status$: Subscription;

  isReloading = false;
  saveSuccess = false;

  error = null;
  showInfoCLUrl = false;
  showInfoCLUsername = false;
  showInfoCLPassword = false;
  showInfoElasticUrls = false;
  showInfoBusId = false;
  showInfoBusPort = false;
  showInfoServerPassword = false;

  constructor(private indexService: IndexService, private settingsService: SettingsService) {
  }

  ngOnInit() {
    this.indexService.getActiveComponentIds().subscribe(
      ids => this.activeIndices = ids
    );
    this.settingsService.get().subscribe(cfg => this.config = cfg);
    this.status$ = this.settingsService.status().subscribe(status => {
      console.log('.');
      this.statusCodelistRepo = status['codelistrepo'] === 'true';
      this.statusElasticsearch = status['elasticsearch'] === 'true';
    });
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

  handleError(error: any) {
    console.error('Error happened: ', error);
    this.error = error.statusText || error.message || error.json().message;
  }
}
