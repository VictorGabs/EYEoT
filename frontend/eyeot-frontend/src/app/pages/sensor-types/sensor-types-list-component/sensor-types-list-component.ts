import { Component, inject } from '@angular/core';
import { ToastComponent } from '../../../shared/toast-component/toast-component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SensorTypesFilterComponent } from './components/sensor-types-filter-component/sensor-types-filter-component';
import { SensorTypesListMainComponent } from './components/sensor-types-list-main-component/sensor-types-list-main-component';
import { SensorTypeService } from '../../../core/services/sensor-type-service/sensor-type-service';
import { SensorTypeFilterDTO } from '../../../core/model/dtos/sensor-type-filter.dto';
import { DialogRegisterSensorTypeComponent } from '../../../shared/dialogs/dialog-register-sensor-type-component/dialog-register-sensor-type-component';

@Component({
  selector: 'app-sensor-types-list-component',
  imports: [MatIconModule, MatButtonModule, MatDialogModule, ToastComponent, SensorTypesFilterComponent, SensorTypesListMainComponent],
  templateUrl: './sensor-types-list-component.html',
  styleUrl: './sensor-types-list-component.scss',
})
export class SensorTypesListComponent {
   readonly dialog = inject (MatDialog);


  constructor(private sensorTypeService:SensorTypeService){
  }

  filtro: SensorTypeFilterDTO = {};

  aplicarFiltro(filtro: SensorTypeFilterDTO) {
    this.filtro = filtro;
  }

  abrirDialogRegisterSensorType(){
    const dialogRef = this.dialog.open(DialogRegisterSensorTypeComponent)
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.sensorTypeService.notificarAlteracao();
      }
    });
  }

}
