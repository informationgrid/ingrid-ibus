/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HitItemComponent } from './hit-item.component';
import { RouterTestingModule } from '@angular/router/testing';
import { IndexService } from '../../+indices/index.service';
import { IndexService as IndexServiceMock } from '../../+indices/index-mock.service';

describe('HitItemComponent', () => {
  let component: HitItemComponent;
  let fixture: ComponentFixture<HitItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HitItemComponent ],
      imports: [
        RouterTestingModule
      ],
      providers: [
        { provide: IndexService, useClass: IndexServiceMock }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HitItemComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    component.hit = {
      id: '123',
      score: 1,
      indexId: 'a',
      title: 'myTitle',
      summary: 'mySummary',
      source: 'mySource',
      detail: '',
      dataSourceName: 'my ds name',
      es_index: '',
      es_type: '',
      hitDetail: {}
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
