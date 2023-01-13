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
import { Component, Input, OnInit } from '@angular/core';
import { SearchHit } from '../SearchHit';
import { Router } from '@angular/router';

@Component({
  selector: 'hit-item',
  templateUrl: './hit-item.component.html',
  styleUrls: ['./hit-item.component.scss']
})
export class HitItemComponent implements OnInit {

  @Input() hit: SearchHit;

  constructor(private router: Router) {
  }

  ngOnInit() {
  }

  showDetailHit(hit: SearchHit) {
    const hitId = hit.hitDetail['0'];
    if (hit.es_index) {
      const isWebsite = hitId.indexOf("http") === 0;
      if (isWebsite) {
        window.open(hitId, '_blank').focus();
      } else {
        this.router.navigate(['/search/' + hit.es_index + '/' + hitId]);
      }
    } else {
      let plugIdEncoded = encodeURIComponent(hit.iPlugId);
      this.router.navigate(['/search/' + plugIdEncoded + '/' + hitId], {
        queryParams: {requestIPlug: true}
      });
    }
  }
}
