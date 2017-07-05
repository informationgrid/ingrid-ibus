import { RouterTestingModule } from '@angular/router/testing';
import { HttpModule } from '@angular/http';
import { IndexService } from './../index.service';
import { IndexItemComponent } from './index-item/index-item.component';
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
      imports: [ 
        HttpModule,
        RouterTestingModule
      ],
      declarations: [ListIndicesComponent, IndexItemComponent],
      providers: [IndexService]
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

  it('should show a list of indices', () => {
    expect(element.queryAll(By.css('.panel')).length).toBe(1);

  });
});