import { Component, OnInit } from '@angular/core';
import {IndexService} from '../index.service';
import {catchError, delay, map} from 'rxjs/operators';

@Component({
  selector: 'app-config-index',
  templateUrl: './config-index.component.html',
  styleUrls: ['./config-index.component.scss']
})
export class ConfigIndexComponent implements OnInit {

  expandState = {};
  entries: any[];
  showInfo = false;

  constructor(private indexSerive: IndexService) { }

  ngOnInit(): void {
    this.fetchEntries();
  }

  fetchEntries() {
    this.indexSerive.getConfigIndexEntries()
      .pipe(map(r => r.sort((a, b) => a.iPlugName?.localeCompare(b.iPlugName))))
      .subscribe( response => this.entries = response)
  }

  deleteEntry(id: string) {
    this.indexSerive.deleteConfigIndexEntry(id)
      .pipe(
        catchError(e => {
          alert(JSON.stringify(e));
          throw e;
        }),
        delay(1000) // give time to fetch updated entries
      )
      .subscribe(() => this.fetchEntries());
  }

  deleteIndex() {
    this.indexSerive.deleteConfigIndex()
      .subscribe( () => this.entries = []);
  }

  stopEventPropagation($event: MouseEvent) {
    $event.stopImmediatePropagation();
  }
}
