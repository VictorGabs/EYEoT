import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { UserResumeDTO } from '../../../../core/model/dtos/user-resume.dto';
import { MatIconModule } from '@angular/material/icon';


export interface ConfirmDeleteUserData {
  usuario: UserResumeDTO;
}

@Component({
  imports: [MatDialogModule,
    MatButtonModule,
    MatIconModule],
  selector: 'app-dialog-confirmar-delecao-usuario',
  styleUrl: './dialog-confirmar-delecao-usuario.scss',
  templateUrl: './dialog-confirmar-delecao-usuario.html',
})
export class DialogConfirmarDelecaoUsuario {
   readonly data = inject<ConfirmDeleteUserData>(MAT_DIALOG_DATA);

  private dialogRef = inject(MatDialogRef<DialogConfirmarDelecaoUsuario>);

  confirmar(): void {
    this.dialogRef.close(true);
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
