import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogConfirmarAlteracaoRoleComponent } from './dialog-confirmar-alteracao-role-component';

describe('DialogConfirmarAlteracaoRoleComponent', () => {
  let component: DialogConfirmarAlteracaoRoleComponent;
  let fixture: ComponentFixture<DialogConfirmarAlteracaoRoleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogConfirmarAlteracaoRoleComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogConfirmarAlteracaoRoleComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
