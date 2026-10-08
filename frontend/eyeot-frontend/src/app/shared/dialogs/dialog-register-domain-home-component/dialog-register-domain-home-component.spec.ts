import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegisterDomainHomeComponent } from './dialog-register-domain-home-component';

describe('DialogRegisterDomainHomeComponent', () => {
  let component: DialogRegisterDomainHomeComponent;
  let fixture: ComponentFixture<DialogRegisterDomainHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogRegisterDomainHomeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogRegisterDomainHomeComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
