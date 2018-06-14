import { IndexService } from '../index.service';
import { IndexItem } from '../index-item/index-item.component';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { IntervalObservable } from 'rxjs/observable/IntervalObservable';
import { Subscription } from 'rxjs/Subscription';
import {startWith, takeWhile} from 'rxjs/operators';

@Component({
  selector: 'app-list-indices',
  templateUrl: './list-indices.component.html',
  styleUrls: ['./list-indices.component.scss']
})
export class ListIndicesComponent implements OnInit, OnDestroy {

  indexItems: IndexItem[] = [];

  isLoading = true;

  showInfo = false;

  // dynamically request connected iPlugs and update page
  autoRefresh = true;

  error = '';

  observer: Subscription;

  expanded = {};

  constructor(private indexService: IndexService) {
  }

  ngOnInit() {
    this.observer = IntervalObservable.create(10000)
      .pipe(
        startWith(0),
        takeWhile( _ => this.autoRefresh )
      )
      .subscribe( () => {
        this.getIndexNames();
      } )
  }

  ngOnDestroy() {
    this.observer.unsubscribe();
  }

  getIndexNames() {
    this.indexService.getIndices().subscribe(
      items => {
        this.indexItems = items;
        this.isLoading = false;
      },
      error => {
        this.error = error;
        this.isLoading = false;
      }
    );
  }

  getIndexItemIdentifier(item: IndexItem) {
    return item.id;
  }

  refresh() {
    this.isLoading = true;
    this.getIndexNames();
  }

}
