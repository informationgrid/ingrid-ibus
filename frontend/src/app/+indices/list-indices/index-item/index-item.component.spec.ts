import { RouterTestingModule } from '@angular/router/testing';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { IndexItemComponent } from './index-item.component';

let ITEM_EXAMPLE = {
  id: "myId",
  name: "myName",
  lastIndexed: "today"
};

describe('IndexItemComponent', () => {
  let component: IndexItemComponent;
  let debug: DebugElement;
  let fixture: ComponentFixture<IndexItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [IndexItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexItemComponent);
    component = fixture.componentInstance;
    debug = fixture.debugElement;
    component.data = ITEM_EXAMPLE;
    fixture.detectChanges();
  });

  it('should create a panel with index information', () => {
    expect(component).toBeTruthy();

    expect(debug.query(By.css('.panel .index-id')).nativeElement.textContent).toBe('myId')
    expect(debug.query(By.css('.panel .index-name')).nativeElement.textContent).toBe('myName')
    expect(debug.query(By.css('.panel .index-last-indexed')).nativeElement.textContent).toBe('today')
  });
});