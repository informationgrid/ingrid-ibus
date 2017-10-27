import { TestBed, inject } from '@angular/core/testing';

import { IPlugService } from './iplug-service.service';

describe('IplugServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [IPlugService]
    });
  });

  it('should be created', inject([IPlugService], (service: IPlugService) => {
    expect(service).toBeTruthy();
  }));
});
