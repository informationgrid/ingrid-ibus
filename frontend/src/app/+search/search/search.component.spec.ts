import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchComponent } from './search.component';
import { SharedModule } from '../../shared/shared.module';
import { SearchModule } from '../search.module';
import { RouterTestingModule } from '@angular/router/testing';
import { IndexService } from '../../+indices/index.service';
import { IndexService as IndexServiceMock } from '../../+indices/index-mock.service';
import { HitItemComponent } from '../hit-item/hit-item.component';
import {HttpClientModule} from '@angular/common/http';

describe('SearchComponent', () => {
  let component: SearchComponent;
  let fixture: ComponentFixture<SearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchComponent, HitItemComponent ],
      imports: [
        RouterTestingModule,
        SharedModule,
        HttpClientModule
      ],
      providers: [
        { provide: IndexService, useClass: IndexServiceMock }
      ]
    })
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
