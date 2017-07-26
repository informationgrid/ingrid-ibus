import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsListComponent } from './settings-list.component';
import { FormsModule } from '@angular/forms';
import { IndexService as IndexServiceMock } from '../../+indices/index-mock.service';
import { IndexService } from '../../+indices/index.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpModule } from '@angular/http';

describe('SettingsListComponent', () => {
  let component: SettingsListComponent;
  let fixture: ComponentFixture<SettingsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SettingsListComponent ],
      imports: [
        FormsModule,
        HttpClientTestingModule,
        HttpModule
      ],
      providers: [
        { provide: IndexService, useClass: IndexServiceMock }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
