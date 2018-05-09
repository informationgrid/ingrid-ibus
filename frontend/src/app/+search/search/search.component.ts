import { Component, OnInit } from '@angular/core';
import { IndexService } from '../../+indices/index.service';
import { SearchHit } from '../SearchHit';
import { SearchHits } from '../SearchHits';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  hits: SearchHit[] = [];
  totalDocs: number;

  error: string = null;

  constructor(private indexService: IndexService) {
  }

  ngOnInit() {
    this.search('');
  }

  search(query: string) {
    this.error = null;

    this.indexService.search(query).subscribe(
      hits => this.prepareHits(hits),
      error => this.error = error
    );
  }

  private prepareHits(hits: SearchHits) {
    this.hits = hits.hits;
    this.totalDocs = hits.length;
  }

}
