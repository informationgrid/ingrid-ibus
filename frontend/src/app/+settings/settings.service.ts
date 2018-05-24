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
