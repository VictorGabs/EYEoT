import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegisterSensorComponent } from './dialog-register-sensor-component';

describe('DialogRegisterSensorComponent', () => {
  let component: DialogRegisterSensorComponent;
  let fixture: ComponentFixture<DialogRegisterSensorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogRegisterSensorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogRegisterSensorComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
