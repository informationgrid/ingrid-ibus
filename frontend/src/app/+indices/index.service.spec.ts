/*-
 * **************************************************-
 * ingrid-ibus-frontend
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
