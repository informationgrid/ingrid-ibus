import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingsListComponent } from './settings-list/settings-list.component';
import { FormsModule } from '@angular/forms';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule
  ],
  declarations: [SettingsListComponent]
})
export class SettingsModule { }
