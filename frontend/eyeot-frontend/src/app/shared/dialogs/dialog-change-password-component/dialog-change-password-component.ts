import { Component, inject } from '@angular/core';
import { ErrorStateMatcher} from '@angular/material/core';
import {
  FormBuilder,
  FormControl,
  FormGroupDirective,
  NgForm,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { validarConfirmacaoSenha } from '../../../core/validators/password-valid-validator';
import { ToastService } from '../../../core/services/toast-service/toast-service';

@Component({
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule],
  selector: 'app-dialog-change-password-component',
  styleUrl: './dialog-change-password-component.scss',
  templateUrl: './dialog-change-password-component.html',
})
export class DialogChangePasswordComponent {
    private fb = inject(FormBuilder);

  constructor(
    private persistenceService: PersistenceService,
    private dialogRef: MatDialogRef<DialogChangePasswordComponent>,
    private toastService: ToastService
  ) {}

  matcherConfirmacaoSenha: ErrorStateMatcher = {
    isErrorState: (
      control: FormControl | null,
      form: FormGroupDirective | NgForm | null
    ): boolean => {
      return !!(
        control &&
        control.invalid &&
        (control.touched || control.dirty)
      ) || !!(
        control &&
        this.form.hasError('passwordMismatch') &&
        (control.touched || control.dirty)
      );
    }
  };

  hide = true;

  salvando = false;
  erro = '';

  form = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [
      Validators.required,
      Validators.minLength(8)
    ]],
    confirmPassword: ['', Validators.required]
  }, {
    validators: validarConfirmacaoSenha
  });

  salvar(): void {
    if (this.form.invalid || this.salvando) {
      this.form.markAllAsTouched();
      return;
    }

    const dados = {
      currentPassword: this.form.controls.currentPassword.value ?? '',
      newPassword: this.form.controls.newPassword.value ?? ''
    };

    this.salvando = true;
    this.erro = '';

    this.persistenceService.putRequest(
      '/v1/auth/me/password',
      dados
    ).subscribe({
      next: (response:any) => {
        this.dialogRef.close(true);
        this.toastService.success(response?.descricao ?? 'Senha alterada com sucesso.');
      },
      error: (error) => {
        this.toastService.error(error?.error?.descricao ?? 'Ocorreu um erro ao alterar a senha.');
        this.salvando = false;
      }
    });
  }

  fechar(): void {
    this.dialogRef.close(false);
  }
}
