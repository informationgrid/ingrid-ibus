import { ConfigService, Configuration } from './../config.service';
import { Observable } from 'rxjs/Observable';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export class PlugDescription {
  proxyServiceUrl: string;
  dataSourceName: string;
  IPLUG_ADMIN_GUI_URL: string;
  dataSourceDescription: string;
  activated: boolean;
  useRemoteElasticsearch: boolean;

  setActive(arg0: any, arg1: any): any {
    throw new Error('Method not implemented.');
  }

}

@Injectable()
export class IPlugService {

  private configuration: Configuration;

  constructor(private http: HttpClient, configService: ConfigService) {
    this.configuration = configService.getConfiguration();
  }

  getConnectedIPlugs(): Observable<PlugDescription[]> {
    return this.http.get<PlugDescription[]>(this.configuration.backendUrl + '/iplugs');
  }

  setActive(id: string, active: boolean): Observable<any> {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(this.configuration.backendUrl + '/iplugs/' + command, {
      id: id
    }, {responseType: 'text'});
  }

}
