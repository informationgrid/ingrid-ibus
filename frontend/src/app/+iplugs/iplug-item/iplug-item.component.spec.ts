/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {IPlugItemComponent} from './iplug-item.component';
import {IPlugService} from '../iplug-service.service';
import {iPlugServiceStub} from '../../../../testing';

describe('IplugItemComponent', () => {
  let component: IPlugItemComponent;
  let fixture: ComponentFixture<IPlugItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IPlugItemComponent],
      providers: [
        {provide: IPlugService, useValue: iPlugServiceStub}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IPlugItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
