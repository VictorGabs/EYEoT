import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { redirectAuthenticationGuard } from './redirect-authentication-guard';

describe('redirectAuthenticationGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => redirectAuthenticationGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
