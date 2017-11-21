import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'iplug-item',
  templateUrl: './iplug-item.component.html',
  styleUrls: ['../../+indices/index-item/index-item.component.css', './iplug-item.component.css']
})
export class IPlugItemComponent implements OnInit {

  @Input() data: any;

  constructor() { }

  ngOnInit() {
  }

}
