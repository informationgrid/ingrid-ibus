import { Observable } from 'rxjs/Observable';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';

export class PlugDescription {
  proxyServiceUrl: string;
  dataSourceName: string;
  dataSourceDescription: string;  setActive(arg0: any, arg1: any): any {
    throw new Error("Method not implemented.");
  }

  activated: boolean;
}

@Injectable()
export class IPlugService {

  constructor(private http: HttpClient) { }

  getConnectedIPlugs(): Observable<PlugDescription[]> {
    return this.http.get<PlugDescription[]>(environment.apiUrl + '/iplugs');
  }

  setActive(id: string, active: boolean): Observable<any> {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(environment.apiUrl + '/iplugs/' + command, {
      id: id
    }, { responseType: 'text' });
  }

}
