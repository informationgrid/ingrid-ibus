import {environment} from 'environments/environment';
import {ConfigService} from './config.service';
import {IndicesModule} from './+indices/indices.module';
import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, LOCALE_ID, NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';

import {AppComponent} from './app.component';
import {IndexService} from './+indices/index-mock.service';
import {SharedModule} from './shared/shared.module';
import {SearchModule} from './+search/search.module';
import {ConfirmationPopoverModule} from 'angular-confirmation-popover';
import {HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {SettingsModule} from './+settings/settings.module';
import {rootRoutes} from './app.routes';
import {IPlugsModule} from 'app/+iplugs/iplugs.module';
import {registerLocaleData} from '@angular/common';
import localeDe from '@angular/common/locales/de';
import {Router} from '@angular/router';

registerLocaleData(localeDe);

export function ConfigLoader(configService: ConfigService) {
  return () => {
    return configService.load(environment.configUrl);
  };
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule, HttpClientXsrfModule,
    rootRoutes,
    ConfirmationPopoverModule.forRoot({
      confirmButtonType: 'danger' // set defaults here
    }),
    SharedModule,
    IPlugsModule,
    IndicesModule,
    SearchModule,
    SettingsModule
  ],
  providers: [
    ConfigService, IndexService,
    {
      provide: APP_INITIALIZER,
      useFactory: ConfigLoader,
      deps: [ConfigService],
      multi: true
    },
    {provide: LOCALE_ID, useValue: 'de'} // <-- use correct locale for dates
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
