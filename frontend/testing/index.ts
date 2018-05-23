import { IndexDetail } from '../src/app/+indices/+index-detail/index-detail.component';
import { IndexItem } from '../src/app/+indices/index-item/index-item.component';
import { DebugElement } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { By } from '@angular/platform-browser';
import {ConfigService} from '../src/app/config.service';
import {IPlugService} from '../src/app/+iplugs/iplug-service.service';

/** Button events to pass to `DebugElement.triggerEventHandler` for RouterLink event handler */
export const ButtonClickEvents = {
  left: {button: 0},
  right: {button: 2}
};

/** Simulate element click. Defaults to mouse left-button click event. */
export function click(el: DebugElement | HTMLElement, eventObj: any = ButtonClickEvents.left): void {
  if (el instanceof HTMLElement) {
    el.click();
  } else {
    el.triggerEventHandler('click', eventObj);
  }
}

export const testIndexItem = <IndexItem>{
  name: 'my-index',
  longName: 'myName',
  lastIndexed: '2015-11-15T11:44:12.000+0000',
  types: [],
  active: true,
  hasLinkedComponent: true
};

/** A mock for the IndexService with some data and spies on the functions. */
export const indexServiceStub = {
  getIndexDetail(id): Observable<IndexDetail> {
    return of(<IndexDetail>{
      name: 'my-index',
      longName: 'myName',
      lastHeartbeat: '2014-03-12T13:37:27.000+0000',
      lastIndexed: '2015-11-15T11:44:12.000+0000',
      mapping: {},
      indexingState: {
        message: 'Indexing ...',
        numProcessed: 45,
        running: true,
        totalDocs: 156
      }
    });
  },
  update: jasmine.createSpy('update'),
  deleteIndex: jasmine.createSpy('deleteIndex'),
  setActive: jasmine.createSpy('setActive'),
  index: jasmine.createSpy('index'),
  getIndices: jasmine.createSpy('getIndices')
};

export function shouldNotShowError(element: DebugElement) {
  expect(element.queryAll(By.css('.error')).length).toBe(0);
}

export function shouldShowError(element: DebugElement, message: string) {
  expect(element.queryAll(By.css('.alert')).length).toBe(1);
  expect(element.query(By.css('.alert')).nativeElement.textContent).toContain(message);
}


let configServiceStubTmpl: Partial<ConfigService> = {
  getConfiguration: () => {
    return {
      backendUrl: '',
      useIndices: true
    }
  }
};

export const configServiceStub = configServiceStubTmpl;

export const iPlugServiceStub = {
  getConnectedIPlugs() {
    return [];
  },

  setActive(id, active) {

  }
};
