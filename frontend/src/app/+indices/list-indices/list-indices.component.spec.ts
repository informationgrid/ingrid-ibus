import { element } from 'protractor';
/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ListIndicesComponent } from './list-indices.component';

describe('ListIndicesComponent', () => {
  let component: ListIndicesComponent;
  let fixture: ComponentFixture<ListIndicesComponent>;
  let element: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ListIndicesComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListIndicesComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the initial page', () => {
    expect(element.queryAll(By.css('h1')).length).toBe(1);
  });

  xit('should show a list of indices', () => {

  });
});