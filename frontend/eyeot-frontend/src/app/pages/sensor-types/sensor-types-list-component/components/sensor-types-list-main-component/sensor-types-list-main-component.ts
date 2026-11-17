import { SensorTypeDataTypeEnum } from './../../../../../core/enuns/sensor-type-data-type.enum';
import { Component, effect, inject, Input, signal, SimpleChanges } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SensorTypeService } from '../../../../../core/services/sensor-type-service/sensor-type-service';
import { SensorTypeDTO } from '../../../../../core/model/dtos/sensor-type.dto';
import { SensorTypeFilterDTO } from '../../../../../core/model/dtos/sensor-type-filter.dto';
import { catchError, of, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogUpdateSensorTypeComponent } from '../../../../../shared/dialogs/dialog-update-sensor-type-component/dialog-update-sensor-type-component';

@Component({
  selector: 'app-sensor-types-list-main-component',
  imports: [MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule],
  templateUrl: './sensor-types-list-main-component.html',
  styleUrl: './sensor-types-list-main-component.scss',
})
export class SensorTypesListMainComponent {

  constructor(private sensorTypeService:SensorTypeService){
    effect(() => {

      this.sensorTypeService.sensorTypeAlterado();

      this.carregarSensorTypes();

    });
  }

  pageNumber = 0;
  pageSize = 10;
  totalElements = 0;

  sensorTypes = signal<SensorTypeDTO[]>([]);

  readonly dialog = inject(MatDialog);
  carregando = signal(true);

  @Input()
  filtro: SensorTypeFilterDTO = {};

  dataSource = new MatTableDataSource<SensorTypeDTO>();

  displayedColumns: string[] = [
    'description',
    'dataType',
    'minValue',
    'maxValue',
    'physicalQuantity',
    'retentionPeriod',
    'summaryPeriod',
    'averageInterval',
    'actions'
  ];

  ngOnChanges(changes: SimpleChanges): void {

    if (
      changes['filtro'] &&
      !changes['filtro'].firstChange
    ) {

      this.pageNumber = 0;

      this.carregarSensorTypes();

    }
  }

  carregarSensorTypes(){

    this.carregando.set(true);

      this.sensorTypeService.listarTiposDeSensores(
      this.pageNumber,
      this.pageSize,
      this.filtro)
          .pipe(
            tap((response: any) => {
              this.sensorTypes.set(response.data ?? []);
              this.totalElements = response.totalElements ?? 0;
              this.pageNumber = response.pageNumber ?? 0;
              this.pageSize = response.pageSize ?? this.pageSize;
              this.dataSource.data = this.sensorTypes();
              this.carregando.set(false);
            }),
            catchError((data: any) => {

              console.error('Erro ao carregar domínios:', data);

              this.sensorTypes.set([]);
              this.totalElements = 0;
              this.carregando.set(false);
              return of(data);
            })
          )
          .subscribe();
  }


  mudouPagina(event: PageEvent) {

    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;

    this.carregarSensorTypes();

  }



    abrirDialogUpdateSensorType(sensorTypeDTO : SensorTypeDTO) {
      const dialogRef = this.dialog.open(DialogUpdateSensorTypeComponent,
            {
              width: '650px',
              maxWidth: '95vw',
              data: {
                sensorType: sensorTypeDTO
              }
            }
          );
      dialogRef.afterClosed().subscribe(resultado => {
        if (resultado === true) {
          this.sensorTypeService.notificarAlteracao();
        }
      });
    }

  protected converterDataType(dataType: number): string {
    switch (dataType) {
      case 1:
        return 'Real';
      case 2:
        return 'Booleano';
      case 3:
        return 'GPS';
      case 4:
        return 'Acelerômetro';
      default:
        return 'Inteiro';
    }
  }


}
