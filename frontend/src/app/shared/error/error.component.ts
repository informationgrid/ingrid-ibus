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
import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'ingrid-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorComponent implements OnInit {

  private _msg: any;

  showResponse = false;

  @Input()
  set msg(message: any) {
    this.showResponse = false;
    if (message && message.url && message.url.indexOf('/login') !== -1) {
      this._msg = 'Sie sind ausgeloggt. Weiterleitung zur Login-Seite in 5 Sekunden';
      setTimeout( () => window.location.replace('./login'), 5000);
    } else {
      this._msg = message;
    }
  }

  get msg(): any {
    return this._msg;
  }

  constructor() {
  }

  ngOnInit() {
  }

  isObject(val: any): boolean {
    return typeof val === 'object';
  }

}
