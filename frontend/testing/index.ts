import { IndexDetail } from '../src/app/+indices/+index-detail/index-detail.component';
import { Observable } from 'rxjs/Rx';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

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


/** A mock for the IndexService with some data and spies on the functions. */
export const indexServiceStub = {
  getIndexDetail(id): Observable<IndexDetail> {
    return Observable.of({
      id: '1',
      name: 'myName',
      lastHeartbeat: '2014-03-12T13:37:27.000+0000',
      lastIndexed:   '2015-11-15T11:44:12.000+0000',
      mapping: {},
      state: 'Indexing ...'
    });
  },
  update: jasmine.createSpy('update'),
  deleteIndex: jasmine.createSpy('update'),
  setActive: jasmine.createSpy('update'),
  index: jasmine.createSpy('update')
};

export function shouldNotShowError(element: DebugElement) {
  expect(element.queryAll(By.css('.error')).length).toBe(0);
}

export function shouldShowError(element: DebugElement, message: string) {
  expect(element.queryAll(By.css('.error')).length).toBe(1);
  expect(element.query(By.css('.error')).nativeElement.textContent).toContain(message);
}
