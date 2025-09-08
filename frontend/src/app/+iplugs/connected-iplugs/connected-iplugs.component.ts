/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import {IPlugInfo, IPlugService} from '../iplug-service.service';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {interval} from 'rxjs';
import {startWith, takeWhile, switchMap} from 'rxjs/operators';

@Component({
    selector: 'app-connected-iplugs',
    templateUrl: './connected-iplugs.component.html',
    styleUrls: ['./connected-iplugs.component.scss'] // TODO: ? '../../+indices/index-item/index-item.component.scss',  ?
    ,
    standalone: false
})
export class ConnectedIplugsComponent implements OnInit, OnDestroy {

  iplugsLocalIndex: IPlugInfo[];
  iplugsCentralIndex: IPlugInfo[];

  showInfo = false;

  // dynamically request connected iPlugs and update page
  autoRefresh = true;

  error = '';

  observer: Subscription;

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {

    this.observer = interval(10000)
      .pipe(
        startWith(0),
        takeWhile(() => this.autoRefresh),
        switchMap(() => this.iPlugService.getConnectedIPlugs())
      )
      .subscribe(
        iPlugs => {
          console.log('iPlugs:', iPlugs);
          this.iplugsLocalIndex = iPlugs
            .filter(p => !p.id.startsWith('__') && !p.useCentralIndex)
            .sort(ConnectedIplugsComponent.compareIPlugInfoName);
          this.iplugsCentralIndex = iPlugs
            .filter(p => !p.id.startsWith('__') && p.useCentralIndex)
            .sort(ConnectedIplugsComponent.compareIPlugInfoName);
          this.error = '';
        },
        error => this.handleError(error)
      );
  }

  ngOnDestroy() {
    this.observer.unsubscribe();
  }

  private static compareIPlugInfoName(info1, info2) {
      return info1.name.localeCompare(info2.name);
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
    this.error = error;
  }

  getIPlugIdentifier(index, item) {
    return item.id;
  }
}
