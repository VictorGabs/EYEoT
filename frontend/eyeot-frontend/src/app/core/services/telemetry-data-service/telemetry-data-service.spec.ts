import { TelemetryDataService } from './telemetry-data-service';
import { TestBed } from '@angular/core/testing';

describe('TelemetryDataService', () => {
  let service: TelemetryDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TelemetryDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
