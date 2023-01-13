/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
import { Router } from '@angular/router';
import { Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { IndexService } from '../index.service';

export class IndexType {
  id: string;
  name: string;
  active: boolean;
  lastIndexed: string;
  hasLinkedComponent?: boolean;
}

export class IndexItem {
  id: string;
  name: string;
  longName: string;
  lastIndexed: string;
  active?: boolean;
  hasLinkedComponent?: boolean;
  adminUrl?: string;
  isConnected: boolean;
  types: IndexType[];
}

@Component({
  selector: 'index-item',
  templateUrl: './index-item.component.html',
  styleUrls: ['./index-item.component.scss']
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
    evt.stopPropagation();
    evt.preventDefault();
    type.active = !type.active;
    this.indexService.setActive(type.id, type.active).subscribe(
      null,
      err => this.onError.next(err)
    );
  }

  index() {
    this.indexService.index(this.data.name).subscribe(
      () => this.dropDownOpen = false,
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

  getIndexTypeItemIdentifier(item: IndexType) {
    return item.id;
  }

  handleError(error: any) {
    console.error('Error happened: ', error);
  }
}
