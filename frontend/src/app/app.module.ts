import { IndicesModule } from './+indices/indices.module';
import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { IndexService } from './+indices/index-mock.service';
import { SharedModule } from './shared/shared.module';
import { SearchModule } from './+search/search.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { SettingsModule } from './+settings/settings.module';
import { rootRoutes } from './app.routes';
import { IPlugsModule } from 'app/+iplugs/iplugs.module';

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
    IndexService,
    { provide: LOCALE_ID, useValue: 'de' } // <-- use correct locale for dates
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
