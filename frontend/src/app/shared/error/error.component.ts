import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'ingrid-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorComponent implements OnInit {

  @Input() msg: string | any;

  constructor() {
  }

  ngOnInit() {
  }

  isObject(val: any): boolean {
    return typeof val === 'object';
  }

}
