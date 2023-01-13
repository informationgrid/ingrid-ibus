/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import {ConfigService, Configuration} from '../config.service';
import {IndexDetail} from './+index-detail/index-detail.component';
import {IndexItem} from './index-item/index-item.component';
import {SearchHit} from '../+search/SearchHit';
import {SearchHits} from '../+search/SearchHits';
import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

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
    return this.http.request<void>('DELETE', this.configuration.backendUrl + '/indices', {body: {id: id}, observe: 'response'});
    // .map(response => response.status);
  }

  setActive(id: string, active: boolean) {
    let command = active ? 'activate' : 'deactivate';
    return this.http.put(this.configuration.backendUrl + '/indices/' + command, {
      id: id
    }, {responseType: 'text'});
  }

  index(id: string) {
    return this.http.put(this.configuration.backendUrl + '/indices/index', {
      id: id
    }, {observe: 'response', responseType: 'text'});
  }

  search(query: string, page: number, numPerPage: number): Observable<SearchHits> {
    return this.http.get<SearchHits>(this.configuration.backendUrl + '/search?query=' + query + '&page=' + page + '&hitsPerPage=' + numPerPage);
  }

  getSearchDetail(indexId: string, hitId: string, requestIPlug?: boolean): Observable<SearchHit> {
    return requestIPlug
      ? this.http.get<SearchHit>(this.configuration.backendUrl + '/iplugs/recordDetail',
        {
          params: {plugId: indexId, docId: hitId}
        })
      : this.http.get<SearchHit>(this.configuration.backendUrl + '/indices/' + indexId + '/' + hitId);
  }

  getActiveComponentIds(): Observable<string[]> {
    return this.http.get<string[]>(this.configuration.backendUrl + '/settings/activeComponentIds', {
      params: new HttpParams().set('verify', 'true')
    });
  }

  getConfigIndexEntries(): Observable<any[]> {

    return this.http.get<any[]>(`${this.configuration.backendUrl}/configIndex`);

  }

  deleteConfigIndexEntry(id: string) {
    return this.http.delete(`${this.configuration.backendUrl}/configIndex/${id}`);
  }

  deleteConfigIndex() {
    return this.http.delete(`${this.configuration.backendUrl}/configIndex`);
  }
}
