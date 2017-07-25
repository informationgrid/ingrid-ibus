import { IndicesModule } from './+indices/indices.module';
import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { IndexService } from './+indices/index-mock.service';
import { SharedModule } from './shared/shared.module';
import { SearchModule } from './+search/search.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import appRoutes from './app.routes';
import { HttpClientModule } from '@angular/common/http';
import { SettingsModule } from './+settings/settings.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    appRoutes,
    ConfirmationPopoverModule.forRoot({
      confirmButtonType: 'danger' // set defaults here
    }),
    SharedModule,
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
