import { ActivatedRoute, ParamMap } from '@angular/router';
import { IndexService } from '../index.service';
import { Component, OnInit } from '@angular/core';

export interface IndexDetail {
  name?: string;
  longName?: string;
  lastIndexed?: string;
  lastHeartbeat?: string;
  mapping?: any;
  indexingState?: any;
  deactivateWhenNoHeartbeat?: boolean;
  active?: boolean;
  [x: string]: any;
}

@Component({
  selector: 'index-detail',
  templateUrl: './index-detail.component.html',
  styleUrls: ['./index-detail.component.css']
})
export class IndexDetailComponent implements OnInit {

  detail: IndexDetail;

  error = null;

  showMapping = false;

  constructor(private activeRoute: ActivatedRoute, private indexService: IndexService) {
  }

  ngOnInit() {
    this.activeRoute.paramMap
      .switchMap((params: ParamMap) => this.indexService.getIndexDetail(params.get('id')))
      .subscribe(detail => this.detail = detail);
  }

  deleteIndex() {
    this.indexService.deleteIndex(this.detail.name).subscribe(
      null,
      err => this.handleError(err)
    );
  }

  toggleActive() {
    this.indexService.setActive(this.detail.name, !this.detail.active).subscribe(
      () => this.detail.active = !this.detail.active,
      err => this.handleError(err)
    );
  }

  index() {
    this.indexService.index(this.detail.name).subscribe(
      null,
      err => this.handleError(err)
    );
  }

  toggleHeartbeatDeactivation() {
    this.detail.deactivateWhenNoHeartbeat = !this.detail.deactivateWhenNoHeartbeat;
    this.indexService.update(this.detail);
    /*this.indexService.update(this.detail).subscribe(
      null,
      err => this.handleError(err)
    );*/
  }

  handleError(error: any) {
    console.error('Error happened: ', error);
    this.error = error.statusText || error.message || error.json().message;
  }

}
