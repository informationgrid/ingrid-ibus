import { IndexService } from '../index.service';
import { IndexItem } from '../index-item/index-item.component';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-indices',
  templateUrl: './list-indices.component.html',
  styleUrls: ['./list-indices.component.css']
})
export class ListIndicesComponent implements OnInit {

  indexItems: IndexItem[] = [];

  isLoading = true;

  error = '';

  constructor(private indexService: IndexService) {
  }

  ngOnInit() {
    this.getIndexNames();
  }

  getIndexNames() {
    this.indexService.getIndices().subscribe(
      items => {
        this.indexItems = items;
        this.isLoading = false;
      },
      error => this.error = error
    );
  }

  refresh() {
    this.isLoading = true;
    this.getIndexNames();
  }

}
