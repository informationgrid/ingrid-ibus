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
import { click, indexServiceStub } from '../../../../testing';
import { IndexService } from '../index.service';
import { RouterTestingModule } from '@angular/router/testing';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { IndexDetailComponent } from './index-detail.component';
import { LOCALE_ID } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { DateRelativePipe } from '../date-relative.pipe';
import { ConfirmationPopoverModule } from 'angular-confirmation-popover';

describe('IndexDetailComponent', () => {
  let component: IndexDetailComponent;
  let fixture: ComponentFixture<IndexDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IndexDetailComponent, DateRelativePipe],
      imports: [
        RouterTestingModule,
        ConfirmationPopoverModule.forRoot(),
        SharedModule
      ],
      providers: [
        {provide: IndexService, useValue: indexServiceStub},
        {provide: LOCALE_ID, useValue: 'de'}
      ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show detail page with all information', () => {
    expect(component).toBeTruthy();

    fixture.detectChanges();

    const detailDiv = <HTMLElement>fixture.debugElement.query(By.css('.detail-container')).nativeElement;

    expect(detailDiv.textContent).toContain('my-index');
    expect(detailDiv.textContent).toContain('myName');
    expect(detailDiv.textContent).toContain('12. März 2014, 14:37:27');
    expect(detailDiv.textContent).toContain('15. Nov. 2015, 12:44:12');
    expect(detailDiv.textContent).toContain('Indexing ...');

    const checkboxHeartbeat = <HTMLInputElement>fixture.debugElement.query(By.css('.footer input')).nativeElement;

    expect(checkboxHeartbeat.checked).toBe(false);
  });

  it('should have set heartbeat disabling checkbox if set', () => {
    component.detail.deactivateWhenNoHeartbeat = true;
    fixture.detectChanges();

    const checkboxHeartbeat = <HTMLInputElement>fixture.debugElement.query(By.css('.footer input')).nativeElement;

    expect(checkboxHeartbeat.checked).toBe(true);
  });

  it('should save index configuration on click (activate/deactivate, heartbeat, delete, index)', () => {
    const checkboxHeartbeat = fixture.debugElement.query(By.css('.footer input'));
    const btnDelete = fixture.debugElement.query(By.css('.footer .deleteIndex'));
    const btnToggleActivate = fixture.debugElement.query(By.css('.footer .toggleActiveIndex'));
    const btnDoIndex = fixture.debugElement.query(By.css('.footer .doIndex'));

    click(checkboxHeartbeat);
    fixture.detectChanges();
    expect(indexServiceStub.update.calls.count()).toBe(1, 'There was no update call.');

    click(btnDelete);
    fixture.detectChanges();
    click(fixture.debugElement.query(By.css('.popover .btn.btn-block')));
    fixture.detectChanges();
    expect(indexServiceStub.deleteIndex.calls.count()).toBe(1, 'There was no delete call.');

    click(btnToggleActivate);
    fixture.detectChanges();
    expect(indexServiceStub.setActive.calls.count()).toBe(1, 'There was no setActive call.');

    click(btnDoIndex);
    fixture.detectChanges();
    expect(indexServiceStub.index.calls.count()).toBe(1, 'There was no index call.');
  });
});
