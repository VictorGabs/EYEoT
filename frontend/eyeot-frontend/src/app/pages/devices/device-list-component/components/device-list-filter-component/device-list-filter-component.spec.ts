import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceListFilterComponent } from './device-list-filter-component';

describe('DeviceListFilterComponent', () => {
  let component: DeviceListFilterComponent;
  let fixture: ComponentFixture<DeviceListFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceListFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeviceListFilterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
