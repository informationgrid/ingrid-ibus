import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchDetailComponent } from './search-detail.component';
import { RouterTestingModule } from '@angular/router/testing';
import { IndexService } from '../../+indices/index.service';
import { IndexService as IndexServiceMock } from '../../+indices/index-mock.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpModule } from '@angular/http';

describe('SearchDetailComponent', () => {
  let component: SearchDetailComponent;
  let fixture: ComponentFixture<SearchDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchDetailComponent ],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        HttpModule
      ],
      providers: [
        {provide: IndexService, useClass: IndexServiceMock}
      ]
    })
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
