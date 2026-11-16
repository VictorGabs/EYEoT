import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface ResetPasswordDialogData {
  usuarioName: string;
  novaSenha: string;
}


@Component({
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
  ],
  selector: 'app-dialog-reset-password-component',
  styleUrl: './dialog-reset-password-component.scss',
  templateUrl: './dialog-reset-password-component.html',
})
export class DialogResetPasswordComponent {

   readonly data = inject<ResetPasswordDialogData>(MAT_DIALOG_DATA);

  private dialogRef = inject(
    MatDialogRef<DialogResetPasswordComponent>
  );

  copiado = false;

  async copiarSenha(): Promise<void> {
    try {
      await navigator.clipboard.writeText(this.data.novaSenha);
      this.copiado = true;
    } catch {
      this.copiado = false;
    }
  }

  fechar(): void {
    this.dialogRef.close();
  }

}
