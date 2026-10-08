import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SensorsListComponent } from './sensors-list-component';

describe('SensorsListComponent', () => {
  let component: SensorsListComponent;
  let fixture: ComponentFixture<SensorsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SensorsListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SensorsListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
