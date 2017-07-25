import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './index-item/index-item.component';
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';

let INDICES: IndexItem[] = [
  {
    id: '12345',
    name: 'myIndex',
    longName: 'IGE-iPlug (HH)',
    lastIndexed: '2014-03-12T13:37:27+00:00',
    active: true,
    hasLinkedComponent: true,
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

  constructor(private http: Http) {
  }

  getIndices(): Observable<IndexItem[]> {
    return Observable.of(INDICES);
  }

  getIndexDetail(id: string): Observable<IndexDetail> {
    return Observable.of(DETAIL);
  }

  update(detail: IndexDetail) {
    // TODO: implement
  }

  deleteIndex(id: string) {
    return Observable.of(null);
  }

  setActive(id: string, active: boolean) {
    return Observable.of(null);
  }

  index(id: string) {
    return Observable.of(null);
  }

  getActiveComponentIds() {

  }
}
