import { Component, EventEmitter, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../../../core/services/auth-service/auth-service';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { DialogProfileComponent } from '../../dialogs/dialog-profile-component/dialog-profile-component';
import { MatDialog } from '@angular/material/dialog';
import { DialogChangePasswordComponent } from '../../dialogs/dialog-change-password-component/dialog-change-password-component';

@Component({
  selector: 'app-header-component',
  imports: [MatToolbarModule,
  MatIconModule,
  MatButtonModule,
  MatSidenavModule,
  MatListModule,
  MatMenuModule,
  MatDividerModule
],
  templateUrl: './header-component.html',
  styleUrl: './header-component.scss',
})
export class HeaderComponent {
  constructor(protected authService:AuthService, private dialog: MatDialog){}
  @Output()
  menuClick = new EventEmitter<void>();

  abrirMenu(): void {
    this.menuClick.emit();
  }

  abrirPerfil(): void {
    this.dialog.open(DialogProfileComponent, {
      width: '450px',
      maxWidth: '95vw',
      disableClose: true
    });
  }

  abrirAlterarSenha(): void {
    this.dialog.open(DialogChangePasswordComponent, {
      width: '450px',
      maxWidth: '95vw'
    });
  }
}
