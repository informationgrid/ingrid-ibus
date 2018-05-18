import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {IPlugItemComponent} from './iplug-item.component';

describe('IplugItemComponent', () => {
  let component: IPlugItemComponent;
  let fixture: ComponentFixture<IPlugItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IPlugItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IPlugItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
