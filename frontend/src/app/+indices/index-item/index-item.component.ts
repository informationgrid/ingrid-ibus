import { Router } from '@angular/router';
import { Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { IndexService } from '../index.service';

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
  isConnected: boolean;
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

  dropDownOpen = false;

  constructor(private eRef: ElementRef, private router: Router, private indexService: IndexService) {
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

  index() {
    this.indexService.index(this.data.name).subscribe(
      null,
      err => this.handleError(err)
    );
  }

  /**
   * Close drop down menu when clicked outside of this component
   * @param event
   */
  @HostListener('document:click', ['$event'])
  closeDropDown(event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.dropDownOpen = false;
    }
  }

  handleError(error: any) {
    console.error('Error happened: ', error);
  }
}
