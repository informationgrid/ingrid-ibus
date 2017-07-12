import { Router } from '@angular/router';
import { Component, OnInit, Input } from '@angular/core';

export interface IndexItem {
  name: string;
  longName: string;
  lastIndexed: string;
  active?: boolean;
  hasAdditionalInfo?: boolean;
}

@Component({
  selector: 'index-item',
  templateUrl: './index-item.component.html',
  styleUrls: ['./index-item.component.css']
})
export class IndexItemComponent implements OnInit {

  @Input() data: IndexItem;

  constructor(private router: Router) { }

  ngOnInit() {
  }

  showIndexItem(item: IndexItem) {
    this.router.navigate(['/indices/' + item.name]);
  }
}
