import { AbstractControl, ValidationErrors } from "@angular/forms";

export function validarConfirmacaoSenha(
  control: AbstractControl
): ValidationErrors | null {
  const novaSenha = control.get('newPassword')?.value;
  const confirmacao = control.get('confirmPassword')?.value;

  if (!novaSenha || !confirmacao) {
    return null;
  }

  return novaSenha === confirmacao
    ? null
    : { passwordMismatch: true };
}
