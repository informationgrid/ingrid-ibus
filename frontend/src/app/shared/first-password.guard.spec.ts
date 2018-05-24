import { TestBed, async, inject } from '@angular/core/testing';

import { FirstPasswordGuard } from './first-password.guard';

describe('FirstPasswordGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FirstPasswordGuard]
    });
  });

  it('should ...', inject([FirstPasswordGuard], (guard: FirstPasswordGuard) => {
    expect(guard).toBeTruthy();
  }));
});
