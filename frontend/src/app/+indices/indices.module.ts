import { IndexDetailComponent } from './list-indices/index-detail/index-detail.component';
import { IndexService } from './index.service';
import { HttpModule } from '@angular/http';
import { IndexItemComponent } from './list-indices/index-item/index-item.component';
import { ListIndicesComponent } from './list-indices/list-indices.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    CommonModule, HttpModule
  ],
  providers: [
    IndexService
  ],
  declarations: [
    ListIndicesComponent,
    IndexItemComponent,
    IndexDetailComponent
  ]
})
export class IndicesModule { }
