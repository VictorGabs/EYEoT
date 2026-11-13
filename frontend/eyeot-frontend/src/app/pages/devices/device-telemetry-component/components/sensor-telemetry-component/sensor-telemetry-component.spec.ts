import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SensorTelemetryComponent } from './sensor-telemetry-component';

describe('SensorTelemetryComponent', () => {
  let component: SensorTelemetryComponent;
  let fixture: ComponentFixture<SensorTelemetryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorTelemetryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorTelemetryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
