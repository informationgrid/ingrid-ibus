import { IndexDetailComponent } from './+index-detail/index-detail.component';
import { IndexService } from './index.service';
import { HttpModule } from '@angular/http';
import { IndexItemComponent } from './index-item/index-item.component';
import { ListIndicesComponent } from './list-indices/list-indices.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { DateRelativePipe } from './date-relative.pipe';

@NgModule({
  imports: [
    CommonModule, HttpModule, SharedModule, ConfirmationPopoverModule
  ],
  providers: [
    IndexService
  ],
  declarations: [
    ListIndicesComponent,
    IndexItemComponent,
    IndexDetailComponent,
    DateRelativePipe
  ]
})
export class IndicesModule { }
