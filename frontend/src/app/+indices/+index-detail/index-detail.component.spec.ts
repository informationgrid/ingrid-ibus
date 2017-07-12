import { click, indexServiceStub } from '../../../../testing/index';
import { IndexService } from '../index.service';
import { RouterTestingModule } from '@angular/router/testing';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { IndexDetailComponent } from './index-detail.component';
import { LOCALE_ID } from '@angular/core';

describe('IndexDetailComponent', () => {
  let component: IndexDetailComponent;
  let fixture: ComponentFixture<IndexDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IndexDetailComponent],
      imports: [
        RouterTestingModule
      ],
      providers: [
        {provide: IndexService, useValue: indexServiceStub},
        { provide: LOCALE_ID, useValue: 'de' }
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

    const detailDiv = <HTMLElement>fixture.debugElement.query(By.css('.detail-container')).nativeElement;

    expect(detailDiv.textContent).toContain('1');
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
    expect(indexServiceStub.update.calls.count()).toBe(1);

    click(btnDelete);
    fixture.detectChanges();
    expect(indexServiceStub.deleteIndex.calls.count()).toBe(1);

    click(btnToggleActivate);
    fixture.detectChanges();
    expect(indexServiceStub.setActive.calls.count()).toBe(1);

    click(btnDoIndex);
    fixture.detectChanges();
    expect(indexServiceStub.index.calls.count()).toBe(1);
  });
});
