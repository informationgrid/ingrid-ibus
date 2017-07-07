import { ActivatedRoute, ParamMap } from '@angular/router';
import { IndexService } from '../../index.service';
import { Component, OnInit } from '@angular/core';

export interface IndexDetail {
  id: string;
  name?: string;
  lastIndexed?: string;
  lastHeartbeat?: string;
  mapping?: any;
  state?: string;
  deactivateWhenNoHeartbeat?: boolean;
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
    this.indexService.deleteIndex(this.detail.id);
  }

  toggleActive() {
    this.activated = !this.activated;
    this.indexService.setActive(this.detail.id, this.activated);
  }

  index() {
    this.indexService.index(this.detail.id);
  }

  toggleHeartbeatDeactivation() {
    this.detail.deactivateWhenNoHeartbeat = !this.detail.deactivateWhenNoHeartbeat;
    this.indexService.update( this.detail );
  }

}
