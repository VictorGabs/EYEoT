import { Component, inject } from '@angular/core';
import { SensorListMainComponent } from './components/sensor-list-main-component/sensor-list-main-component';
import { SensorFilterComponent } from './components/sensor-filter-component/sensor-filter-component';
import { MatIcon } from '@angular/material/icon';
import { ToastComponent } from '../../../shared/toast-component/toast-component';
import { SensorService } from '../../../core/services/sensor-service/sensor-service';
import { MatDialog } from '@angular/material/dialog';
import { SensorFilterDTO } from '../../../core/model/dtos/sensor-filter.dto';
import { DialogRegisterSensorComponent } from '../../../shared/dialogs/dialog-register-sensor-component/dialog-register-sensor-component';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-sensors-list-component',
  imports: [SensorListMainComponent, SensorFilterComponent, MatIcon, ToastComponent, MatButtonModule],
  templateUrl: './sensors-list-component.html',
  styleUrl: './sensors-list-component.scss',
})
export class SensorsListComponent {

   readonly dialog = inject (MatDialog);


  constructor(private sensorService:SensorService){
  }

  filtro: SensorFilterDTO = {};

  aplicarFiltro(filtro: SensorFilterDTO) {
    this.filtro = filtro;
  }

  abrirDialogRegisterSensor(){
    const dialogRef = this.dialog.open( DialogRegisterSensorComponent,
      {
        height: '750px',
        maxWidth: '95vw'
      }
    );
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.sensorService.notificarAlteracao();
      }
    });
  }
}
