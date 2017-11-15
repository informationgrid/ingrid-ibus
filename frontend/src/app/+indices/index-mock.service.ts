import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './index-item/index-item.component';
import 'rxjs/add/operator/map';
import { SearchHits } from '../+search/SearchHits';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

let INDICES: IndexItem[] = [
  {
    id: '12345',
    name: 'myIndex',
    longName: 'IGE-iPlug (HH)',
    lastIndexed: '2014-03-12T13:37:27+00:00',
    active: true,
    hasLinkedComponent: true,
    isConnected: true,
    types: [
      {
        id: '1',
        name: 'myType1',
        active: true,
        lastIndexed: '2016-03-12T13:37:27+00:00'
      }, {
        id: '2',
        name: 'myType2',
        active: false,
        lastIndexed: '2014-03-12T13:37:27+00:00'
      }
    ]
  }
];

let DETAIL: IndexDetail = {
  id: 'myIndex',
  name: 'IGE-iPlug (HH)',
  lastIndexed: '2014-03-12T13:37:27+00:00',
  lastHeartbeat: '2017-07-05T15:22:48+00:00',
  state: 'Indexing ...',
  deactivateWhenNoHeartbeat: false,
  mapping: {}
};

@Injectable()
export class IndexService {

  constructor(private http: HttpClient) {
  }

  getIndices(): Observable<IndexItem[]> {
    return of(INDICES);
  }

  getIndexDetail(id: string): Observable<IndexDetail> {
    return of(DETAIL);
  }

  getSearchDetail(): Observable<any> {
    return of({});
  }

  search(): Observable<SearchHits> {
    return of(
      {
        length: 0,
        hits: []
      }
    )
  }

  update(detail: IndexDetail) {
    // TODO: implement
  }

  deleteIndex(id: string) {
    return of(null);
  }

  setActive(id: string, active: boolean) {
    return of(null);
  }

  index(id: string) {
    return of(null);
  }

  getActiveComponentIds() {
    return of(['a', 'b']);
  }
}
