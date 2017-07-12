import { RouterTestingModule } from '@angular/router/testing';
import { HttpModule } from '@angular/http';
import { IndexService } from '../index.service';
import { IndexItemComponent } from './index-item/index-item.component';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ListIndicesComponent } from './list-indices.component';
import { Observable } from 'rxjs/Observable';
import { shouldNotShowError, shouldShowError } from '../../../../testing/index';

describe('ListIndicesComponent', () => {
  let component: ListIndicesComponent;
  let fixture: ComponentFixture<ListIndicesComponent>;
  let element: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule,
        RouterTestingModule
      ],
      declarations: [ListIndicesComponent, IndexItemComponent],
      providers: [IndexService]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListIndicesComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should show the initial page', () => {
    fixture.detectChanges();
    expect(element.queryAll(By.css('.page-header')).length).toBe(1);
  });

  it('should show a list of indices', () => {
    fixture.detectChanges();
    expect(element.queryAll(By.css('.panel')).length).toBe(1);
    shouldNotShowError(element);

  });

  it('should show an error if indices could not be fetched', () => {
    const service = fixture.debugElement.injector.get(IndexService);
    spyOn(service, 'getIndices').and.returnValue(Observable.throw('fake error'));

    fixture.detectChanges();
    shouldShowError(element, 'fake error');

  });



});
