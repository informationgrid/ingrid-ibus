import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {IPlugItemComponent} from './iplug-item.component';
import {IPlugService} from '../iplug-service.service';
import {iPlugServiceStub} from '../../../../testing';

describe('IplugItemComponent', () => {
  let component: IPlugItemComponent;
  let fixture: ComponentFixture<IPlugItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IPlugItemComponent],
      providers: [
        {provide: IPlugService, useValue: iPlugServiceStub}
      ]
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
