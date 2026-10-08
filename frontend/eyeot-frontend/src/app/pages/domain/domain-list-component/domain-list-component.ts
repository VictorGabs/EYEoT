import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { DomainListFilterComponent } from './components/domain-list-filter-component/domain-list-filter-component';
import { DialogRegisterDomainHomeComponent } from '../../../shared/dialogs/dialog-register-domain-home-component/dialog-register-domain-home-component';
import { DomainListMainComponent } from './components/domain-list-main-component/domain-list-main-component';
import { ToastComponent } from '../../../shared/toast-component/toast-component';
import { DomainFilterDTO } from '../../../core/model/dtos/domain-filter.dto';
import { DomainService } from '../../../core/services/domain-service/domain-service';

@Component({
  selector: 'app-domain-list-component',
  imports: [MatIconModule, MatButtonModule, MatDialogModule, DomainListFilterComponent, DomainListMainComponent, ToastComponent],
  templateUrl: './domain-list-component.html',
  styleUrl: './domain-list-component.scss',
})
export class DomainListComponent {


  readonly dialog = inject (MatDialog);


  constructor(private domainService:DomainService){
  }

  filtro: DomainFilterDTO = {};

  aplicarFiltro(filtro: DomainFilterDTO) {
    this.filtro = filtro;
  }

  abrirDialogRegisterDomainHome(){
    const dialogRef = this.dialog.open(DialogRegisterDomainHomeComponent,
          {
            width: '650px',
            maxWidth: '95vw',
          }
        );
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.domainService.notificarAlteracao();
      }
    });
  }


}
