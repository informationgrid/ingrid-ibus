import {ErrorComponent} from './error/error.component';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import { NormalizeIdPipe } from './normalize-id.pipe';

@NgModule({
  imports: [
    BrowserModule
  ],
  declarations: [
    ErrorComponent,
    NormalizeIdPipe
  ],
  exports: [
    ErrorComponent,
    NormalizeIdPipe
  ]
})
export class SharedModule { }
