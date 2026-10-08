import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorListMainComponent } from './sensor-list-main-component';

describe('SensorListMainComponent', () => {
  let component: SensorListMainComponent;
  let fixture: ComponentFixture<SensorListMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorListMainComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorListMainComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
