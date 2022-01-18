/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ConfigService, Configuration} from '../config.service';
import {BehaviorSubject, Observable} from 'rxjs/Rx';
import {flatMap, tap} from 'rxjs/operators';

export interface AppConfiguation {
  'codelistrepo.url'?: string;
  'codelistrepo.username'?: string;
  'codelistrepo.password'?: string;
  'elastic.remoteHosts'?: string;
  'spring.security.user.password'?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private configuration: Configuration;
  appConfiguration = new BehaviorSubject<AppConfiguation>({});

  constructor(private http: HttpClient, configService: ConfigService) {
    this.configuration = configService.getConfiguration();
  }

  get(): Observable<AppConfiguation> {
    return this.http.get<AppConfiguation>(this.configuration.backendUrl + '/settings')
      .pipe(
        tap(cfg => this.appConfiguration.next(cfg))
      );
  }

  save(config: AppConfiguation): Observable<null> {
    return this.http.post<null>(this.configuration.backendUrl + '/settings', config);
  }

  status() {
    return Observable
      .timer(0, 10000)
      .pipe(
        flatMap(() => this.http.get<any>(this.configuration.backendUrl + '/status'))
      );
  }
}
