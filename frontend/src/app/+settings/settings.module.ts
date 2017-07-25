import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingsListComponent } from './settings-list/settings-list.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  declarations: [SettingsListComponent]
})
export class SettingsModule { }
