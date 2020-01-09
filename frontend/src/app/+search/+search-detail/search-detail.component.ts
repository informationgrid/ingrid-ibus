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
import {Component, OnInit} from '@angular/core';
import {IndexService} from '../../+indices/index.service';
import {ActivatedRoute} from '@angular/router';
import {SearchHit} from '../SearchHit';
import {Observable} from "rxjs";

@Component({
  selector: 'search-detail',
  templateUrl: './search-detail.component.html',
  styleUrls: ['./search-detail.component.scss']
})
export class SearchDetailComponent implements OnInit {

  detail: SearchHit;

  constructor(private activeRoute: ActivatedRoute, private indexService: IndexService) {
  }

  ngOnInit() {
    const queryParams = this.activeRoute.snapshot.queryParams;
    const routeParams = this.activeRoute.snapshot.params;

    const requestIPlug = queryParams['requestIPlug'];
    this.indexService.getSearchDetail(routeParams['indexId'], routeParams['hitId'], requestIPlug === 'true')
      .subscribe(detail => this.detail = detail);
  }

}
