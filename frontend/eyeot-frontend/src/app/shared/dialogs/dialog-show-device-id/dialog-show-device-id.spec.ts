import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogShowDeviceId } from './dialog-show-device-id';

describe('DialogShowDeviceId', () => {
  let component: DialogShowDeviceId;
  let fixture: ComponentFixture<DialogShowDeviceId>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogShowDeviceId],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogShowDeviceId);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
