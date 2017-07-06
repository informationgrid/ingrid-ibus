import { IndexDetail } from './list-indices/index-detail/index-detail.component';
import { IndexItem } from './list-indices/index-item/index-item.component';
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';

let INDICES: IndexItem[] = [
    { id: 'myIndex', name: 'IGE-iPlug (HH)', lastIndexed: '2017-07-05 17:23:00' }
];

let DETAIL = { id: 'myIndex', name: 'IGE-iPlug (HH)', lastIndexed: '2017-07-05 17:23:00' };

@Injectable()
export class IndexService {

    constructor(private http: Http) { }

    getIndices(): Observable<IndexItem[]> {
        // return this.http.get('').map( response => {} );
        return Observable.of(INDICES);
    }

    getIndexDetail(id: string): Observable<IndexDetail> {
        return Observable.of(DETAIL);
    }

}