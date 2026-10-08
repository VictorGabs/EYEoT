import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegisterSensorTypeComponent } from './dialog-register-sensor-type-component';

describe('DialogRegisterSensorTypeComponent', () => {
  let component: DialogRegisterSensorTypeComponent;
  let fixture: ComponentFixture<DialogRegisterSensorTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogRegisterSensorTypeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogRegisterSensorTypeComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
