import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IplugItemComponent } from './iplug-item.component';

describe('IplugItemComponent', () => {
  let component: IplugItemComponent;
  let fixture: ComponentFixture<IplugItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IplugItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IplugItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
