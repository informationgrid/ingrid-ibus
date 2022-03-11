/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
import { ActivatedRoute, ParamMap } from '@angular/router';
import { IndexService } from '../index.service';
import { Component, OnInit } from '@angular/core';
import 'rxjs/add/operator/switchMap';

export class IndexDetail {
  id: string;
  name?: string;
  longName?: string;
  lastIndexed?: string;
  lastHeartbeat?: string;
  mapping?: any;
  indexingState?: any;
  deactivateWhenNoHeartbeat?: boolean;
  active?: boolean;
  [x: string]: any;
}

@Component({
  selector: 'index-detail',
  templateUrl: './index-detail.component.html',
  styleUrls: ['./index-detail.component.scss']
})
export class IndexDetailComponent implements OnInit {

  detail: IndexDetail;

  error = null;

  showMapping = false;

  constructor(private activeRoute: ActivatedRoute, private indexService: IndexService) {
  }

  ngOnInit() {
    this.activeRoute.paramMap
      .switchMap((params: ParamMap) => {
        return this.indexService.getIndexDetail(params.get('id'), params.get('type'));
      })
      .subscribe(detail => this.detail = detail);
  }

  deleteIndex() {
    this.indexService.deleteIndex(this.detail.name).subscribe(
      null,
      err => this.handleError(err)
    );
  }

  toggleActive() {
    this.indexService.setActive(this.detail.id, !this.detail.active).subscribe(
      () => this.detail.active = !this.detail.active,
      err => this.handleError(err)
    );
  }

  index() {
    this.indexService.index(this.detail.name).subscribe(
      null,
      err => this.handleError(err)
    );
  }

  toggleHeartbeatDeactivation() {
    this.detail.deactivateWhenNoHeartbeat = !this.detail.deactivateWhenNoHeartbeat;
    this.indexService.update(this.detail);
    /*this.indexService.update(this.detail).subscribe(
      null,
      err => this.handleError(err)
    );*/
  }

  handleError(error: any) {
    console.error('Error happened: ', error);
    this.error = error.statusText || error.message || error.json().message;
  }

}
