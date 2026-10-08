import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegisterDeviceComponent } from './dialog-register-device-component';

describe('DialogRegisterDeviceComponent', () => {
  let component: DialogRegisterDeviceComponent;
  let fixture: ComponentFixture<DialogRegisterDeviceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogRegisterDeviceComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogRegisterDeviceComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
