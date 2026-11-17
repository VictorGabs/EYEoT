import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DomainCardComponent } from './domain-card-component';

describe('DomainCardComponent', () => {
  let component: DomainCardComponent;
  let fixture: ComponentFixture<DomainCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DomainCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
