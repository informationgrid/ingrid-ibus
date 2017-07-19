import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchComponent } from './search/search.component';
import { SharedModule } from '../shared/shared.module';
import { HitItemComponent } from './hit-item/hit-item.component';
import { SearchDetailComponent } from './+search-detail/search-detail.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [SearchComponent, HitItemComponent, SearchDetailComponent]
})
export class SearchModule { }
