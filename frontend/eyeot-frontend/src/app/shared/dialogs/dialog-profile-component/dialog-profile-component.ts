import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { AuthService } from '../../../core/services/auth-service/auth-service';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { UserDTO } from '../../../core/model/dtos/user.dto';
import { UserUpdateFormDTO } from '../../../core/model/dtos/user-update-form-dto';
import { ToastService } from '../../../core/services/toast-service/toast-service';

@Component({
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule],
  selector: 'app-dialog-profile-component',
  styleUrl: './dialog-profile-component.scss',
  templateUrl: './dialog-profile-component.html',
})
export class DialogProfileComponent {

  constructor(
    protected authService: AuthService,
    private persistenceService: PersistenceService,
    private dialogRef: MatDialogRef<DialogProfileComponent>,
    private toastService: ToastService
  ) {
    this.carregarDados();
  }

  private fb = inject(FormBuilder);

  editando = false;

  salvando = false;
  erro = '';

  form = this.fb.group({
    nameUser: [{ value: '', disabled: true }, Validators.required],
    phone: [{ value: '', disabled: true }],
    email2: [{ value: '', disabled: true }, Validators.email]
  });


  get usuario(): UserDTO | null {
    return this.authService.usuarioLogado();
  }

  private carregarDados(): void {
    const usuario = this.usuario;

    if (!usuario) {
      this.dialogRef.close();
      return;
    }

    this.form.patchValue({
      nameUser: usuario.nameUser,
      phone: usuario.phone ?? '',
      email2: usuario.email2 ?? ''
    });
  }

  habilitarEdicao(): void {
    this.erro = '';
    this.editando = true;

    this.form.enable();
  }

  cancelarEdicao(): void {
    this.carregarDados();

    this.form.disable();

    this.erro = '';
    this.editando = false;
  }

  salvar(): void {
    if (this.form.invalid || this.salvando) {
      this.form.markAllAsTouched();
      return;
    }

    const dados: UserUpdateFormDTO = {
      nameUser: (this.form.controls.nameUser.value ?? '').trim(),
      phone: (this.form.controls.phone.value ?? '').trim() || null,
      email2: (this.form.controls.email2.value ?? '').trim() || null
    };

    this.salvando = true;
    this.erro = '';

    this.persistenceService.putRequest('/v1/auth/me', dados)
      .subscribe({
        next: (resposta: any) => {
          const usuarioAtualizado = resposta.data as UserDTO;

          this.authService.usuarioLogado.set(usuarioAtualizado);

          this.form.disable();

          this.editando = false;
          this.salvando = false;
          this.dialogRef.close();
          this.toastService.success('Dados atualizados com sucesso.');
        },
        error: (error) => {
          this.erro =
            error.error?.descricao ??
            'Não foi possível atualizar seus dados.';

          this.salvando = false;
        }
      });
  }

  fechar(): void {
    this.dialogRef.close();
  }

}
