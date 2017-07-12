import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'ingrid-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  @Input() msg;

  constructor() { }

  ngOnInit() {
  }

}
