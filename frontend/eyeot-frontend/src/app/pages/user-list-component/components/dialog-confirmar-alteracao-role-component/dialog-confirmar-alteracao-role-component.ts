import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { UserDTO } from '../../../../core/model/dtos/user.dto';
import { RolesEnum } from '../../../../core/enuns/roles-enum';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  imports: [MatDialogModule,
    MatButtonModule,
    MatIconModule],
  selector: 'app-dialog-confirmar-alteracao-role-component',
  styleUrl: './dialog-confirmar-alteracao-role-component.scss',
  templateUrl: './dialog-confirmar-alteracao-role-component.html',
})
export class DialogConfirmarAlteracaoRoleComponent {
  constructor(
    private dialogRef:
      MatDialogRef<DialogConfirmarAlteracaoRoleComponent>,

    @Inject(MAT_DIALOG_DATA)
    public data: {
      usuario: UserDTO;
      novoPapel: RolesEnum;
    }
  ) {}


  cancelar(): void {

    this.dialogRef.close(false);

  }


  confirmar(): void {

    this.dialogRef.close(true);

  }
}
