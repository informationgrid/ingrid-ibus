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
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ConfigService, Configuration} from '../config.service';
import {catchError, map, tap} from 'rxjs/operators';
import {BehaviorSubject, Observable, Subject, of} from 'rxjs';

export interface AppConfiguation {
  'codelistrepo.url'?: string;
  'codelistrepo.username'?: string;
  'codelistrepo.password'?: string;
  'elastic.remoteHosts'?: string;
  'elastic.username'?: string;
  'elastic.password'?: string;
  'elastic.sslTransport'?: boolean;
  'spring.security.user.password'?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private configuration: Configuration;
  appConfiguration = new BehaviorSubject<AppConfiguation>({});

  $status = new Subject();

  constructor(private http: HttpClient, configService: ConfigService) {
    this.configuration = configService.getConfiguration();
  }

  get(): Observable<AppConfiguation> {
    return this.http.get<AppConfiguation>(this.configuration.backendUrl + '/settings')
      .pipe(
        map(cfg => {
          // @ts-ignore
          cfg["elastic.sslTransport"] = cfg["elastic.sslTransport"] === "true";
          return cfg
        }),
        tap(cfg => this.appConfiguration.next(cfg))
      );
  }

  save(config: AppConfiguation): Observable<null> {
    return this.http.post<null>(this.configuration.backendUrl + '/settings', config);
  }

  status() {
    this.http.get<any>(this.configuration.backendUrl + '/status').pipe(
      catchError(e => {
        console.error(e);
        return of({})
      })
    ).subscribe(data => this.$status.next(data));
  }
}
