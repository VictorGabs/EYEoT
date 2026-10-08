import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceListMainComponent } from './device-list-main-component';

describe('DeviceListMainComponent', () => {
  let component: DeviceListMainComponent;
  let fixture: ComponentFixture<DeviceListMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceListMainComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeviceListMainComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
