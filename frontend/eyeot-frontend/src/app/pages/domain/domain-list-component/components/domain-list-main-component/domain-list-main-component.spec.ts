import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainListMainComponent } from './domain-list-main-component';

describe('DomainListMainComponent', () => {
  let component: DomainListMainComponent;
  let fixture: ComponentFixture<DomainListMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainListMainComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DomainListMainComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
