import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigIndexComponent } from './config-index.component';

describe('ConfigIndexComponent', () => {
  let component: ConfigIndexComponent;
  let fixture: ComponentFixture<ConfigIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigIndexComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
