import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeviceTelemetryComponent } from './device-telemetry-component';

describe('DeviceTelemetryComponent', () => {
  let component: DeviceTelemetryComponent;
  let fixture: ComponentFixture<DeviceTelemetryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceTelemetryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeviceTelemetryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
