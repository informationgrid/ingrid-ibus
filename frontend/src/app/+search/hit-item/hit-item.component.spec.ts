import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HitItemComponent } from './hit-item.component';
import { RouterTestingModule } from '@angular/router/testing';
import { IndexService } from '../../+indices/index.service';
import { IndexService as IndexServiceMock } from '../../+indices/index-mock.service';

describe('HitItemComponent', () => {
  let component: HitItemComponent;
  let fixture: ComponentFixture<HitItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HitItemComponent ],
      imports: [
        RouterTestingModule
      ],
      providers: [
        { provide: IndexService, useClass: IndexServiceMock }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HitItemComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    component.hit = {
      id: '123',
      score: 1,
      indexId: 'a',
      title: 'myTitle',
      summary: 'mySummary',
      source: 'mySource',
      detail: '',
      dataSourceName: 'my ds name',
      es_index: '',
      es_type: '',
      hitDetail: {}
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
