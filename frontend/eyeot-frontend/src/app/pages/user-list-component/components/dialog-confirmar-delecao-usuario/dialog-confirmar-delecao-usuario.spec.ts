import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogConfirmarDelecaoUsuario } from './dialog-confirmar-delecao-usuario';

describe('DialogConfirmarDelecaoUsuario', () => {
  let component: DialogConfirmarDelecaoUsuario;
  let fixture: ComponentFixture<DialogConfirmarDelecaoUsuario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogConfirmarDelecaoUsuario],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogConfirmarDelecaoUsuario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
