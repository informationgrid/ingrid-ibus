import { Component, OnInit } from '@angular/core';
import { IndexService } from '../../+indices/index.service';
import { ActivatedRoute } from '@angular/router';
import { SearchHit } from '../SearchHit';

@Component({
  selector: 'search-detail',
  templateUrl: './search-detail.component.html',
  styleUrls: ['./search-detail.component.scss']
})
export class SearchDetailComponent implements OnInit {

  detail: SearchHit;

  constructor(private activeRoute: ActivatedRoute, private indexService: IndexService) { }

  ngOnInit() {
    this.activeRoute.paramMap
      .switchMap(params => this.indexService.getSearchDetail(params.get('indexId'), params.get('hitId')))
      .subscribe(
        detail => this.detail = detail
      )
  }

}
