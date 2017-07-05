import { HttpModule } from '@angular/http';
import { RouterTestingModule } from '@angular/router/testing';
/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { IndexService } from './index.service';

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
      expect(items[0].id).toBe('myIndex');
      expect(items[0].name).toBe('IGE-iPlug (HH)');
      expect(items[0].lastIndexed).toBe('2017-07-05 17:23:00');
    });
  }));
});