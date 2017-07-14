import { Component, OnInit } from '@angular/core';
import { IndexService } from '../../+indices/index.service';
import { SearchHit } from '../SearchHit';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  hits: SearchHit[];

  error: string = null;

  constructor(private indexService: IndexService) { }

  ngOnInit() {
  }

  search(query: string) {
    this.error = null;

    this.indexService.search(query).subscribe(
      hits => this.hits = hits,
      error => this.error = error
    );
  }

}
