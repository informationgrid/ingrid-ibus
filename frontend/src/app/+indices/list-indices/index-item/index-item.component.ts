import { Router } from '@angular/router';
import { Component, OnInit, Input } from '@angular/core';

export interface IndexItem {
  id: string;
  name: string;
  lastIndexed: string;
  activated?: boolean;
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

  showIndexItem(item) {
    this.router.navigate(['/indices/' + item.id]);
  }
}
