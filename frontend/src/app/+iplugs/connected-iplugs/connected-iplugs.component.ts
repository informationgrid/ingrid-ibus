import { PlugDescription, IPlugService } from '../iplug-service.service';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { IntervalObservable } from 'rxjs/observable/IntervalObservable';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import {startWith, takeWhile} from 'rxjs/operators';

@Component({
  selector: 'app-connected-iplugs',
  templateUrl: './connected-iplugs.component.html',
  styleUrls: ['./connected-iplugs.component.scss']
})
export class ConnectedIplugsComponent implements OnInit, OnDestroy {

  iplugsLocalIndex: PlugDescription[];
  iplugsCentralIndex: PlugDescription[];

  showInfo = false;

  // dynamically request connected iPlugs and update page
  autoRefresh = true;

  error = '';

  observer: Subscription;

  constructor(private iPlugService: IPlugService) { }

  ngOnInit() {

    this.observer = IntervalObservable.create(10000)
      .pipe(
        startWith(0),
        takeWhile(_ => this.autoRefresh )
      )
      .subscribe( () => {
        this.iPlugService.getConnectedIPlugs()
          .subscribe(
          iPlugs => {
            this.iplugsLocalIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && !p.useRemoteElasticsearch);
            this.iplugsCentralIndex = iPlugs.filter(p => !p.proxyServiceUrl.startsWith('__') && p.useRemoteElasticsearch);
            this.error = '';
          },
          error => this.handleError( error )
        )
      } );
  }

  ngOnDestroy() {
    this.observer.unsubscribe();
  }

  private handleError(error) {
    console.error( 'Problem getting connected iPlugs', error );
    this.error = error;
  }

}
