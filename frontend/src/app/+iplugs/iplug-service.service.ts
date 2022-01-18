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
import { ConfigService, Configuration } from './../config.service';
import { Observable } from 'rxjs/Observable';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export class IPlugInfo {
  id: string;
  name: string;
  adminUrl: string;
  description: string;
  active: boolean;
  useCentralIndex: boolean;

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

  getConnectedIPlugs(): Observable<IPlugInfo[]> {
    return this.http.get<IPlugInfo[]>(this.configuration.backendUrl + '/iplugs');
  }

  setActive(id: string, active: boolean): Observable<any> {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(this.configuration.backendUrl + '/iplugs/' + command, {
      id: id
    }, {responseType: 'text'});
  }

}
