import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './index-item/index-item.component';
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import { environment } from '../../environments/environment';
import { SearchHit } from '../+search/SearchHit';

@Injectable()
export class IndexService {

  constructor(private http: Http) {
  }

  getIndices(): Observable<IndexItem[]> {
    return this.http.get(environment.apiUrl + '/indices')
      .map(response => response.json());
  }

  getIndexDetail(id: string): Observable<IndexDetail> {
    return this.http.get(environment.apiUrl + '/indices/' + id)
      .map(response => response.json());
  }

  update(detail: IndexDetail) {
    // TODO: implement
  }

  deleteIndex(id: string): Observable<number> {
    return this.http.delete(environment.apiUrl + '/indices/' + id)
      .map(response => response.status);
  }

  setActive(id: string, active: boolean) {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(environment.apiUrl + '/indices/' + id + '/' + command, null);
  }

  index(id: string) {
    return this.http.put(environment.apiUrl + '/indices/' + id + '/index', null)
      .map(response => response.status);
  }

  search(query: string): Observable<SearchHit[]> {
    return this.http.get(environment.apiUrl + '/search?query=' + query)
      .map(res => res.json());
  }
}
