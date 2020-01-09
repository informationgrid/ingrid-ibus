/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
import { Component, OnInit } from '@angular/core';
import { IndexService } from '../../+indices/index.service';
import { SearchHit } from '../SearchHit';
import {DebugEvent, SearchHits} from '../SearchHits';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  numPerPage = 10;

  hits: SearchHit[] = [];
  totalDocs: number;
  pages: number[] = [];
  currentPage = 0;

  error: string = null;
  showDebug = false;
  debugInfo: DebugEvent[];

  constructor(private indexService: IndexService) {
  }

  ngOnInit() {
    this.search('');
  }

  search(query: string, page: number = 0) {
    this.error = null;
    this.currentPage = page;

    this.indexService.search(query, page, this.numPerPage).subscribe(
      hits => this.prepareHits(hits),
      error => this.error = error
    );
  }

  private prepareHits(hits: SearchHits) {
    this.hits = hits.hits;
    this.totalDocs = hits.length;

    // calculate the number of pages to show
    let numPages = Math.round(this.totalDocs / this.numPerPage);
    if (this.totalDocs > this.numPerPage && this.totalDocs % this.numPerPage !== 0) {
      numPages += 1;
    } else if(this.totalDocs <= this.numPerPage) {
      numPages = 1;
    }

    this.pages = Array(numPages).fill(0).map((x, i)=>i);
    this.debugInfo = hits.debug;
  }

}
