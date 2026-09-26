import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainListFilterComponent } from './domain-list-filter-component';

describe('DomainListFilterComponent', () => {
  let component: DomainListFilterComponent;
  let fixture: ComponentFixture<DomainListFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainListFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DomainListFilterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
