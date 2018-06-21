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
import { PlugDescription, IPlugService } from '../iplug-service.service';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { IntervalObservable } from 'rxjs/observable/IntervalObservable';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import {startWith, takeWhile} from 'rxjs/operators';

@Component({
  selector: 'app-connected-iplugs',
  templateUrl: './connected-iplugs.component.html',
  styleUrls: ['./connected-iplugs.component.scss'] // TODO: ? '../../+indices/index-item/index-item.component.scss',  ?
})
export class ConnectedIplugsComponent implements OnInit, OnDestroy {

  iplugsLocalIndex: PlugDescription[];
  iplugsCentralIndex: PlugDescription[];

  showInfo = false;

  // dynamically request connected iPlugs and update page
  autoRefresh = true;

  error = '';

  observer: Subscription;

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {

    this.observer = IntervalObservable.create(10000)
      .pipe(
        startWith(0),
        takeWhile(_ => this.autoRefresh )
      )
      .subscribe( () => {
        this.iPlugService.getConnectedIPlugs()
          .subscribe(
          iPlugs => {
            this.iplugsLocalIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && !p.useRemoteElasticsearch);
            this.iplugsCentralIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && p.useRemoteElasticsearch);
            this.error = '';
          },
          error => this.handleError( error )
        )
      } );
  }

  ngOnDestroy() {
    this.observer.unsubscribe();
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
    this.error = error;
  }

  getIPlugIdentifier(index, item) {
    return item.proxyServiceUrl;
  }
}
