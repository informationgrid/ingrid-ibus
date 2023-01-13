/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export class Configuration {
    constructor(
      public backendUrl: string,
      public useIndices: boolean) { }
}

@Injectable()
export class ConfigService {

    private config: Configuration;

    constructor(private http: HttpClient) { }

    load(url: string): Promise<void> {
        console.log('=== ConfigService ===');

        return new Promise((resolve) => {
            this.http.get<Configuration>(url).subscribe(config => {
                this.config = config;
                resolve();
            });
        });
    }

    getConfiguration(): Configuration {
        return this.config;
    }
}
