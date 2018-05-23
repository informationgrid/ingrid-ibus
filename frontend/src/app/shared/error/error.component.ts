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
