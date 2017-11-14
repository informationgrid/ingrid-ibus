import { ErrorComponent } from './error/error.component';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports: [
    BrowserModule
  ],
  declarations: [
    ErrorComponent
  ],
  exports: [
    ErrorComponent
  ]
})
export class SharedModule { }
