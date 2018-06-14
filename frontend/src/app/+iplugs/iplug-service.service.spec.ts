import { TestBed, inject } from '@angular/core/testing';

import { IPlugService } from './iplug-service.service';
import {HttpClientModule} from '@angular/common/http';
import {ConfigService} from '../config.service';
import {configServiceStub} from '../../../testing';

describe('IplugServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        IPlugService,
        { provide: ConfigService, useValue: configServiceStub }
      ]
    });
  });

  it('should be created', inject([IPlugService], (service: IPlugService) => {
    expect(service).toBeTruthy();
  }));
});
