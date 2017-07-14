import { Component, Input, OnInit } from '@angular/core';
import { SearchHit } from '../SearchHit';

@Component({
  selector: 'hit-item',
  templateUrl: './hit-item.component.html',
  styleUrls: ['./hit-item.component.css']
})
export class HitItemComponent implements OnInit {

  @Input() hit: SearchHit;

  constructor() { }

  ngOnInit() {
  }

}
