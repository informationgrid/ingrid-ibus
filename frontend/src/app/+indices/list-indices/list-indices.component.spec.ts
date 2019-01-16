/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
import { RouterTestingModule } from '@angular/router/testing';
import { IndexItemComponent } from '../index-item/index-item.component';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ListIndicesComponent } from './list-indices.component';
import { indexServiceStub, shouldNotShowError, shouldShowError, testIndexItem } from '../../../../testing/index';
import { IndexService } from '../index.service';
import { SharedModule } from '../../shared/shared.module';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs/observable/of';
import {throwError} from 'rxjs/internal/observable/throwError';

describe('ListIndicesComponent', () => {
  let component: ListIndicesComponent;
  let fixture: ComponentFixture<ListIndicesComponent>;
  let element: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        RouterTestingModule,
        ConfirmationPopoverModule.forRoot({
          confirmButtonType: 'danger' // set defaults here
        }),
        SharedModule
      ],
      declarations: [ListIndicesComponent, IndexItemComponent],
      providers: [{provide: IndexService, useValue: indexServiceStub}]
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
    indexServiceStub.getIndices.and.callFake(() => of([]));
    fixture.detectChanges();
    expect(element.queryAll(By.css('.page-header')).length).toBe(2);
  });

  it('should show a list of indices', () => {
    indexServiceStub.getIndices.and.callFake(() => of([testIndexItem]));
    fixture.detectChanges();
    expect(element.queryAll(By.css('.panel')).length).toBe(1);
    shouldNotShowError(element);

  });

  it('should show an error if indices could not be fetched', () => {
    const service = fixture.debugElement.injector.get(IndexService);
    indexServiceStub.getIndices.and.returnValue(throwError('fake error'));

    fixture.detectChanges();
    shouldShowError(element, 'fake error');

  });

  it('should show an error if indices could not be activated', () => {
    const service = fixture.debugElement.injector.get(IndexService);
    indexServiceStub.setActive.and.returnValue(throwError('fake error'));

    fixture.detectChanges();
    shouldShowError(element, 'fake error');

  });


});
