/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
import {Component} from '@angular/core';
import {ConfigService} from './config.service';
import {SettingsService} from './+settings/settings.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    standalone: false
})
export class AppComponent {
  showMenu = false;
  showIndicesPage = true;
  version: string;
  timestamp: string;

  constructor(private config: ConfigService, settingsService: SettingsService, router: Router) {
    if (config.getConfiguration().useIndices === false) {
      this.showIndicesPage = false;
    }
    settingsService.get().subscribe( appCfg => {
      if (appCfg['needPasswordChange'] === 'true') {
        router.navigate(['/settings']);
      }
      this.version = appCfg['version'];
      this.timestamp = appCfg['timestamp'];
    })
  }

}
