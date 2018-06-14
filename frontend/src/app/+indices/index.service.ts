import { ConfigService, Configuration } from '../config.service';
import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './index-item/index-item.component';
import { SearchHit } from '../+search/SearchHit';
import { SearchHits } from '../+search/SearchHits';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class IndexService {

  private configuration: Configuration;

  constructor(private http: HttpClient, configService: ConfigService) {
    this.configuration = configService.getConfiguration();
  }

  getIndices(): Observable<IndexItem[]> {
    return this.http.get<IndexItem[]>(this.configuration.backendUrl + '/indices');
  }

  getIndexDetail(id: string, type: string): Observable<IndexDetail> {
    return this.http.get<IndexDetail>(this.configuration.backendUrl + '/indices/' + id + '?type=' + type);
  }

  update(detail: IndexDetail) {
    // TODO: implement
  }

  deleteIndex(id: string): Observable<HttpResponse<void>> {
    return this.http.request<void>( 'DELETE',  this.configuration.backendUrl + '/indices', {body: { id: id }, observe: 'response'});
      // .map(response => response.status);
  }

  setActive(id: string, active: boolean) {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(this.configuration.backendUrl + '/indices/' + command, {
      id: id
    }, { responseType: 'text' });
  }

  index(id: string) {
    return this.http.put(this.configuration.backendUrl + '/indices/index', {
      id: id
    }, {observe: 'response', responseType: 'text'});
  }

  search(query: string): Observable<SearchHits> {
    return this.http.get<SearchHits>(this.configuration.backendUrl + '/search?query=' + query);
  }

  getSearchDetail(indexId: string, hitId: string): Observable<SearchHit> {
    return this.http.get<SearchHit>(this.configuration.backendUrl + '/indices/' + indexId + '/' + hitId);
  }

  getActiveComponentIds(): Observable<string[]> {
    return this.http.get<string[]>(this.configuration.backendUrl + '/settings/activeComponentIds', {
      params: new HttpParams().set('verify', 'true')
    });
  }
}
