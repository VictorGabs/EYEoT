import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainHomeSectionComponent } from './domain-home-section-component';

describe('DomainHomeSectionComponent', () => {
  let component: DomainHomeSectionComponent;
  let fixture: ComponentFixture<DomainHomeSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainHomeSectionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DomainHomeSectionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
