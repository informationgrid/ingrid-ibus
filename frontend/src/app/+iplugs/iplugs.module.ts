import { SharedModule } from './../shared/shared.module';
import { IPlugService } from './iplug-service.service';
import { NgModule } from '@angular/core';

import { ConnectedIplugsComponent } from './connected-iplugs/connected-iplugs.component';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [CommonModule, SharedModule],
    exports: [],
    declarations: [ConnectedIplugsComponent],
    providers: [IPlugService],
})
export class IPlugsModule { }
