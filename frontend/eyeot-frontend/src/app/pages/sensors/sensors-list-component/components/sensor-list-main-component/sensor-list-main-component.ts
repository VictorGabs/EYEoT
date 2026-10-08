import { Component, effect, Input, signal, SimpleChanges } from '@angular/core';
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

  constructor(private sensorService:SensorService){
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

  @Input()
  filtro: SensorTypeFilterDTO = {};

  dataSource = new MatTableDataSource<SensorTypeDTO>();

  displayedColumns: string[] = [
    'index',
    'deviceId',
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


  mudouPagina(event: PageEvent) {

    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;

    this.carregarSensores();

  }



}
