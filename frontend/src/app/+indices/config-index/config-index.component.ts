/*-
 * **************************************************-
 * InGrid iBus Frontend
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
import { Component, OnInit } from '@angular/core';
import {IndexService} from '../index.service';
import {catchError, delay, map} from 'rxjs/operators';

@Component({
  selector: 'app-config-index',
  templateUrl: './config-index.component.html',
  styleUrls: ['./config-index.component.scss']
})
export class ConfigIndexComponent implements OnInit {

  expandState = {};
  entries: any[];
  showInfo = false;

  constructor(private indexSerive: IndexService) { }

  ngOnInit(): void {
    this.fetchEntries();
  }

  fetchEntries() {
    this.indexSerive.getConfigIndexEntries()
      .pipe(map(r => r.sort((a, b) => a.iPlugName?.localeCompare(b.iPlugName))))
      .subscribe( response => this.entries = response)
  }

  deleteEntry(id: string) {
    this.indexSerive.deleteConfigIndexEntry(id)
      .pipe(
        catchError(e => {
          alert(JSON.stringify(e));
          throw e;
        }),
        delay(1000) // give time to fetch updated entries
      )
      .subscribe(() => this.fetchEntries());
  }

  deleteIndex() {
    this.indexSerive.deleteConfigIndex()
      .subscribe( () => this.entries = []);
  }

  stopEventPropagation($event: MouseEvent) {
    $event.stopImmediatePropagation();
  }
}
