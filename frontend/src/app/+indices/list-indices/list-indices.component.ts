/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
import {IndexService} from '../index.service';
import {IndexItem} from '../index-item/index-item.component';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';
import {Subscription} from 'rxjs/Subscription';
import {map, startWith, takeWhile} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-list-indices',
  templateUrl: './list-indices.component.html',
  styleUrls: ['./list-indices.component.scss']
})
export class ListIndicesComponent implements OnInit, OnDestroy {

  indexItems: IndexItem[] = [];
  otherIndexItems: IndexItem[] = [];

  isLoading = true;

  showInfo = false;

  // dynamically request connected iPlugs and update page
  autoRefresh = true;

  error = '';

  observer: Subscription;

  expanded = {};

  constructor(private indexService: IndexService,
              private router: Router) {
  }

  ngOnInit() {
    this.observer = IntervalObservable.create(10000)
      .pipe(
        startWith(0),
        takeWhile(_ => this.autoRefresh)
      )
      .subscribe(() => this.getIndexNames());
  }

  ngOnDestroy() {
    this.observer.unsubscribe();
  }

  getIndexNames() {
    this.indexService.getIndices()
      .pipe(
        map(items => items
          .sort((a, b) => {
            if (a.hasLinkedComponent || b.hasLinkedComponent) {
              if (!a.hasLinkedComponent) return 1;
              if (!b.hasLinkedComponent) return -1;

              const firstPart = a.longName?.localeCompare(b.longName);
              return firstPart === 0 || firstPart === undefined
                ? (a.types || [])[0]?.name?.localeCompare((b.types || [])[0]?.name)
                : firstPart;
            } else {
              return a.name?.localeCompare(b.name);
            }
          })
        )
      )
      .subscribe(items => {
          this.error = '';
          this.indexItems = items.filter(item => item.hasLinkedComponent);
          this.otherIndexItems = items.filter(item => !item.hasLinkedComponent);
          this.isLoading = false;
        },
        error => {
          this.error = error;
          this.isLoading = false;
        }
      );
  }

  getIndexItemIdentifier(item: IndexItem) {
    return item.id || item.name;
  }

  refresh() {
    this.isLoading = true;
    this.getIndexNames();
  }

  showConfigIndex() {
    this.router.navigate(['/configIndex']);
  }
}
