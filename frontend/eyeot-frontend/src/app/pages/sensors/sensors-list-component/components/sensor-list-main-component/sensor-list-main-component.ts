import { Component, effect, inject, Input, signal, SimpleChanges } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SensorTypeDTO } from '../../../../../core/model/dtos/sensor-type.dto';
import { SensorTypeFilterDTO } from '../../../../../core/model/dtos/sensor-type-filter.dto';
import { catchError, of, tap } from 'rxjs';
import { SensorService } from '../../../../../core/services/sensor-service/sensor-service';
import { MatDialog } from '@angular/material/dialog';
import { DialogUpdateSensorComponent } from '../../../../../shared/dialogs/dialog-update-sensor-component/dialog-update-sensor-component';
import { SensorDTO } from '../../../../../core/model/dtos/sensor.dto';
import { ToastService } from '../../../../../core/services/toast-service/toast-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sensor-list-main-component',
  imports: [MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule],
  templateUrl: './sensor-list-main-component.html',
  styleUrl: './sensor-list-main-component.scss',
})
export class SensorListMainComponent {

  constructor(private sensorService:SensorService, private toastService:ToastService){
    effect(() => {

      this.sensorService.sensorAlterado();

      this.carregarSensores();

    });
  }

  pageNumber = 0;
  pageSize = 10;
  totalElements = 0;

  sensors = signal<SensorTypeDTO[]>([]);

  carregando = signal(true);

  readonly dialog = inject(MatDialog)

  private readonly router = inject(Router);

  @Input()
  filtro: SensorTypeFilterDTO = {};

  dataSource = new MatTableDataSource<SensorTypeDTO>();

  displayedColumns: string[] = [
    'index',
    'deviceId',
    'deviceOwnerName',
    'deviceOwnerEmail',
    'id_sensortype',
    'description',
    'physicalQuantity',
    'measureUnit',
    'actions'
  ];

  ngOnChanges(changes: SimpleChanges): void {

    if (
      changes['filtro'] &&
      !changes['filtro'].firstChange
    ) {

      this.pageNumber = 0;

      this.carregarSensores();

    }
  }

  carregarSensores(){

    this.carregando.set(true);

      this.sensorService.listarMeusSensores(
      this.pageNumber,
      this.pageSize,
      this.filtro)
          .pipe(
            tap((response: any) => {
              this.sensors.set(response.data ?? []);
              this.totalElements = response.totalElements ?? 0;
              this.pageNumber = response.pageNumber ?? 0;
              this.pageSize = response.pageSize ?? this.pageSize;
              this.dataSource.data = this.sensors();
              this.carregando.set(false);
            }),
            catchError((data: any) => {

              console.error('Erro ao carregar domínios:', data);

              this.sensors.set([]);
              this.totalElements = 0;
              this.carregando.set(false);
              return of(data);
            })
          )
          .subscribe();
  }

  abrirDialogUpdateSensor(sensorDTO : SensorDTO) {
    const dialogRef = this.dialog.open(DialogUpdateSensorComponent,
          {
            width: '650px',
            maxWidth: '95vw',
            data: {
              sensor: sensorDTO
            }
          }
        );
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.sensorService.notificarAlteracao();
      }
    });
  }

  visualizarDispositivo(idDevice: number): void {
    this.router.navigate(['/devices', idDevice, 'telemetry']);
  }



  mudouPagina(event: PageEvent) {

    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;

    this.carregarSensores();

  }


  excluirSensor(sensorDTO : SensorDTO) {
    this.sensorService.deletarSensor(sensorDTO.id_device, sensorDTO.index)
      .pipe(
        tap((response: any) => {
          this.toastService.success(response.descricao || 'Sensor excluído com sucesso.');
          this.sensorService.notificarAlteracao();
        }),
        catchError((error: any) => {
          this.toastService.error(error.descricao || 'Erro ao excluir sensor.');
          console.error('Erro ao excluir sensor:', error);
          return of(error);
        })
      )
      .subscribe();
  }


}
