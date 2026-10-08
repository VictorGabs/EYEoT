import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorTypesFilterComponent } from './sensor-types-filter-component';

describe('SensorTypesFilterComponent', () => {
  let component: SensorTypesFilterComponent;
  let fixture: ComponentFixture<SensorTypesFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorTypesFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorTypesFilterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
