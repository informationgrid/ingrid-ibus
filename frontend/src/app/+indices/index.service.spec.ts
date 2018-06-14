import { HttpModule } from '@angular/http';

import { TestBed, inject } from '@angular/core/testing';
import { IndexService } from './index-mock.service';

describe('Service: Index', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [IndexService]
    });
  });

  it('should get a list of index items', inject([IndexService], (service: IndexService) => {
    expect(service).toBeTruthy();

    service.getIndices().subscribe(items => {
      expect(items.length).toBe(1);
      expect(items[0].name).toBe('myIndex');
      expect(items[0].longName).toBe('IGE-iPlug (HH)');
      expect(items[0].lastIndexed).toBe('2014-03-12T13:37:27+00:00');
    });
  }));
});
