import { ConfigService, Configuration } from './../config.service';
import { IndexDetail } from './+index-detail/index-detail.component';
import { IndexItem } from './index-item/index-item.component';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import { environment } from '../../environments/environment';
import { SearchHit } from '../+search/SearchHit';
import { SearchHits } from '../+search/SearchHits';
import { HttpClient, HttpParams } from '@angular/common/http';

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

  deleteIndex(id: string): Observable<number> {
    return this.http.delete(this.configuration.backendUrl + '/indices/' + id, {observe: 'response'})
      .map(response => response.status);
  }

  setActive(id: string, active: boolean) {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(this.configuration.backendUrl + '/indices/' + encodeURIComponent(id) + '/' + command, null, { responseType: 'text' });
  }

  index(id: string) {
    return this.http.put(this.configuration.backendUrl + '/indices/' + id + '/index', null, {observe: 'response'})
      .map(response => response.status);
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
