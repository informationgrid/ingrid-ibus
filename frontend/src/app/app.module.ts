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
import {environment} from 'environments/environment';
import {ConfigService} from './config.service';
import {IndicesModule} from './+indices/indices.module';
import {BrowserModule} from '@angular/platform-browser';
import { LOCALE_ID, NgModule, inject, provideAppInitializer } from '@angular/core';
import {FormsModule} from '@angular/forms';

import {AppComponent} from './app.component';
import {IndexService} from './+indices/index-mock.service';
import {SharedModule} from './shared/shared.module';
import {SearchModule} from './+search/search.module';
import {ConfirmationPopoverModule} from 'angular-confirmation-popover';
import { provideHttpClient, withInterceptorsFromDi, withXsrfConfiguration } from '@angular/common/http';
import {SettingsModule} from './+settings/settings.module';
import {rootRoutes} from './app.routes';
import {IPlugsModule} from 'app/+iplugs/iplugs.module';
import {registerLocaleData} from '@angular/common';
import localeDe from '@angular/common/locales/de';

registerLocaleData(localeDe);

export function ConfigLoader(configService: ConfigService) {
  return () => {
    return configService.load(environment.configUrl);
  };
}

@NgModule({ declarations: [
        AppComponent
    ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        FormsModule,
        rootRoutes,
        ConfirmationPopoverModule.forRoot({
            confirmButtonType: 'danger' // set defaults here
        }),
        SharedModule,
        IPlugsModule,
        IndicesModule,
        SearchModule,
        SettingsModule], providers: [
        ConfigService, IndexService,
        provideAppInitializer(() => {
        const initializerFn = (ConfigLoader)(inject(ConfigService));
        return initializerFn();
      }),
        { provide: LOCALE_ID, useValue: 'de' } // <-- use correct locale for dates
        ,
        provideHttpClient(withInterceptorsFromDi(), withXsrfConfiguration({}))
    ] })
export class AppModule {
}

