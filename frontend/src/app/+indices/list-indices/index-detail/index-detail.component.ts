import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { IndexService } from './../../index.service';
import { Component, OnInit } from '@angular/core';

export interface IndexDetail {
  id: string;
  name?: string;
  lastIndexed?: string;
  [x: string]: any;
}

@Component({
  selector: 'index-detail',
  templateUrl: './index-detail.component.html',
  styleUrls: ['./index-detail.component.css']
})
export class IndexDetailComponent implements OnInit {

  detail: IndexDetail;

  activated = false;

  constructor(private activeRoute: ActivatedRoute, private indexService: IndexService) { }

  ngOnInit() {
    this.activeRoute.paramMap
      .switchMap((params: ParamMap) => this.indexService.getIndexDetail(params.get('id')))
      .subscribe(detail => this.detail = detail);
  }

  deleteIndex() {

  }

  toggleActive() {
    this.activated = !this.activated;
  }

  index() {

  }

}