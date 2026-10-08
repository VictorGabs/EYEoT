import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorTypesListComponent } from './sensor-types-list-component';

describe('SensorTypesListComponent', () => {
  let component: SensorTypesListComponent;
  let fixture: ComponentFixture<SensorTypesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorTypesListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorTypesListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
