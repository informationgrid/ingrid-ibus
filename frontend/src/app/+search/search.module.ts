import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchComponent } from './search/search.component';
import { SharedModule } from '../shared/shared.module';
import { HitItemComponent } from './hit-item/hit-item.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [SearchComponent, HitItemComponent]
})
export class SearchModule { }
