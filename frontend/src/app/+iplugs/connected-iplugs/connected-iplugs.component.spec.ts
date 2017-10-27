import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectedIplugsComponent } from './connected-iplugs.component';

describe('ConnectedIplugsComponent', () => {
  let component: ConnectedIplugsComponent;
  let fixture: ComponentFixture<ConnectedIplugsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConnectedIplugsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectedIplugsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
