/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import { Component, Input, OnInit } from '@angular/core';
import { IPlugService, IPlugInfo } from '../iplug-service.service';

@Component({
  selector: 'iplug-item',
  templateUrl: './iplug-item.component.html',
  styleUrls: ['../../+indices/index-item/index-item.component.scss', './iplug-item.component.scss']
})
export class IPlugItemComponent implements OnInit {

  @Input() component: any;
  @Input() showActivateToggle = true;

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {
  }

  activateIPlug(iPlug: IPlugInfo) {
    iPlug.active = !iPlug.active;
    this.iPlugService.setActive( iPlug.id, iPlug.active ).subscribe(
      null,
      error => this.handleError(error)
    );
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
  }
}
