import { Router } from '@angular/router';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { IndexService } from '../index.service';
import { URLSearchParams } from '@angular/http';

export class IndexType {
  id: string;
  name: string;
  active: boolean;
  lastIndexed: string;
}

export class IndexItem {
  id: string;
  name: string;
  longName: string;
  lastIndexed: string;
  active?: boolean;
  hasLinkedComponent?: boolean;
  types: IndexType[];
}

@Component({
  selector: 'index-item',
  templateUrl: './index-item.component.html',
  styleUrls: ['./index-item.component.css']
})
export class IndexItemComponent implements OnInit {

  @Input() data: IndexItem;
  @Input() view = 'full';

  @Output() onDelete = new EventEmitter();

  @Output() onError = new EventEmitter();

  constructor(private router: Router, private indexService: IndexService) {
  }

  ngOnInit() {
  }

  showIndexItem(item: IndexItem, type: string) {
    if (item.hasLinkedComponent) {

      this.router.navigate(['/indices/' + item.name, {type: type}]);
    }
  }

  deleteIndex(item: IndexItem) {
    this.indexService.deleteIndex(item.name).subscribe(
      response => this.onDelete.next()
    );
  }

  activateIndexType(type: IndexType, evt: Event) {
    evt.stopImmediatePropagation();
    type.active = !type.active;
    this.indexService.setActive(type.id, type.active).subscribe(
      null,
      err => this.onError.next(err)
    );
  }
}
