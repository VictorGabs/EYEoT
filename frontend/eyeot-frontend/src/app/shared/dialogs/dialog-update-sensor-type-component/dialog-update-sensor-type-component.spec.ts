import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogUpdateSensorTypeComponent } from './dialog-update-sensor-type-component';

describe('DialogUpdateSensorTypeComponent', () => {
  let component: DialogUpdateSensorTypeComponent;
  let fixture: ComponentFixture<DialogUpdateSensorTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogUpdateSensorTypeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogUpdateSensorTypeComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
