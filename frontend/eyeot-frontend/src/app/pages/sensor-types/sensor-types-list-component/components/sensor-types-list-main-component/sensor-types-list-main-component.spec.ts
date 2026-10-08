import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorTypesListMainComponent } from './sensor-types-list-main-component';

describe('SensorTypesListMainComponent', () => {
  let component: SensorTypesListMainComponent;
  let fixture: ComponentFixture<SensorTypesListMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorTypesListMainComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorTypesListMainComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
