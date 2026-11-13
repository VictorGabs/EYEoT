import { MatButtonModule } from '@angular/material/button';
import { Component, inject } from '@angular/core';
import { ToastComponent } from '../../../shared/toast-component/toast-component';
import { MatIcon } from '@angular/material/icon';
import { DeviceFilterDTO } from '../../../core/model/dtos/device-filter.dto';
import { DeviceService } from '../../../core/services/device-service/device-service';
import { MatDialog } from '@angular/material/dialog';
import { DeviceListFilterComponent } from './components/device-list-filter-component/device-list-filter-component';
import { DeviceListMainComponent } from './components/device-list-main-component/device-list-main-component';
import { DialogRegisterDeviceComponent } from '../../../shared/dialogs/dialog-register-device-component/dialog-register-device-component';
import { ActivatedRoute } from '@angular/router';
import { DialogShowDeviceId } from '../../../shared/dialogs/dialog-show-device-id/dialog-show-device-id';
import { DeviceResumeDTO } from '../../../core/model/dtos/device-resume.dto';

@Component({
  selector: 'app-device-list-component',
  imports: [MatButtonModule, MatIcon, DeviceListFilterComponent, DeviceListMainComponent, ToastComponent],
  templateUrl: './device-list-component.html',
  styleUrl: './device-list-component.scss',
})
export class DeviceListComponent {

  readonly dialog = inject(MatDialog);

  constructor(
    private deviceService: DeviceService,
    private route: ActivatedRoute
  ) {

    const idDomain = this.route.snapshot.queryParamMap.get('idDomain');

    if (idDomain) {

      const id = Number(idDomain);

      if (!Number.isNaN(id)) {

        this.filtro = {
          idDomain: id
        };

      }

    }

  }

  filtro: DeviceFilterDTO = {};




  aplicarFiltro(filtro: DeviceFilterDTO): void {
    this.filtro = filtro;
  }

  abrirDialog() {

    const dialogRef =
      this.dialog.open(
        DialogRegisterDeviceComponent,
        {
          width: '650px',
          maxWidth: '95vw'
        }
      );

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado?.success) {

        this.deviceService.notificarAlteracao();

        this.dialog.open(DialogShowDeviceId, {
            width: '460px',
            maxWidth: '95vw',
            disableClose: false,
            data: {
              deviceId: resultado.deviceId
            }
          });

        }
    });
  }



}
