import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HitItemComponent } from './hit-item.component';

describe('HitItemComponent', () => {
  let component: HitItemComponent;
  let fixture: ComponentFixture<HitItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HitItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HitItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
