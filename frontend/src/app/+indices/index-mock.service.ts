import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './list-indices/index-item/index-item.component';
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';

let INDICES: IndexItem[] = [
  {name: 'myIndex', longName: 'IGE-iPlug (HH)', lastIndexed: '2014-03-12T13:37:27+00:00', active: true}
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
    // return this.http.get('').map( response => {} );
    return Observable.of(INDICES);
  }

  getIndexDetail(id: string): Observable<IndexDetail> {
    return Observable.of(DETAIL);
  }

  update(detail: IndexDetail) {
    // TODO: implement
  }

  deleteIndex(id: string) {
    // TODO: implement
  }

  setActive(id: string, active: boolean) {
    // TODO: implement
  }

  index(id: string) {
    // TODO: implement
  }

}
