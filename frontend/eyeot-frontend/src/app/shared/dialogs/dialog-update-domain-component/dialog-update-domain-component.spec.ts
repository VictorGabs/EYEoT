import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogUpdateDomainComponent } from './dialog-update-domain-component';

describe('DialogUpdateDomainComponent', () => {
  let component: DialogUpdateDomainComponent;
  let fixture: ComponentFixture<DialogUpdateDomainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogUpdateDomainComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogUpdateDomainComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
