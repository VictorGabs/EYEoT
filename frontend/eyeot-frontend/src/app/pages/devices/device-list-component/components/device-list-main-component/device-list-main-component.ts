import {
  Component,
  effect,
  inject,
  Input,
  signal,
  SimpleChanges
} from '@angular/core';

import {
  MatTableDataSource,
  MatTableModule
} from '@angular/material/table';

import {
  MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

import { catchError, of, tap } from 'rxjs';

import { DeviceFilterDTO } from '../../../../../core/model/dtos/device-filter.dto';

import { DeviceService } from '../../../../../core/services/device-service/device-service';
import { DeviceResumeDTO } from '../../../../../core/model/dtos/device-resume.dto';
import { DatePipe } from '@angular/common';
import { DialogRegisterSensorComponent } from '../../../../../shared/dialogs/dialog-register-sensor-component/dialog-register-sensor-component';
import { MatDialog } from '@angular/material/dialog';
import { DialogUpdateDeviceComponent } from '../../../../../shared/dialogs/dialog-update-device-component/dialog-update-device-component';
import { Router } from '@angular/router';
import { ToastService } from '../../../../../core/services/toast-service/toast-service';

@Component({
  selector: 'app-device-list-main-component',
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    DatePipe
  ],
  templateUrl: './device-list-main-component.html',
  styleUrl: './device-list-main-component.scss',
})
export class DeviceListMainComponent {

  constructor(private deviceService: DeviceService,
    private dialogAdicionarSensor: MatDialog,
    private toastService: ToastService
  ) {

    effect(() => {

      this.deviceService.deviceAlterado();

      this.carregarDispositivos();

    });
  }

  readonly dialog = inject(MatDialog);


  private readonly router = inject(Router);

  devices = signal<DeviceResumeDTO[]>([]);
  carregando = signal(true);

  pageNumber = 0;
  pageSize = 10;
  totalElements = 0;

  @Input()
  filtro: DeviceFilterDTO = {};


  dataSource = new MatTableDataSource<DeviceResumeDTO>();

  displayedColumns: string[] = [

    'deviceId',

    'domain',

    'ownerName',

    'ownerEmail',

    'numSensors',

    'enable',

    'creationDate',

    'actions'

  ];

  ngOnChanges(changes: SimpleChanges): void {

    if (
      changes['filtro'] &&
      !changes['filtro'].firstChange
    ) {

      this.pageNumber = 0;

      this.carregarDispositivos();

    }

  }


  carregarDispositivos(): void {

    this.carregando.set(true);

    this.deviceService
      .listarMeusDispositivos(
        this.pageNumber,
        this.pageSize,
        this.filtro
      )
      .pipe(

        tap((response: any) => {

          this.devices.set(
            response.data ?? []
          );

          this.totalElements =
            response.totalElements ?? 0;

          this.pageNumber =
            response.pageNumber ?? 0;

          this.pageSize =
            response.pageSize ?? this.pageSize;

          this.dataSource.data =
            this.devices();

          this.carregando.set(false);

        }),

        catchError((error: any) => {

          this.devices.set([]);

          this.dataSource.data = [];

          this.totalElements = 0;

          this.carregando.set(false);


          return of(error);

        })

      )
      .subscribe();

  }

  mudouPagina(event: PageEvent): void {

    this.pageNumber =
      event.pageIndex;

    this.pageSize =
      event.pageSize;


    this.carregarDispositivos();

  }


  abrirDialogUpdateDevice(deviceResumeDTO: DeviceResumeDTO) {
    const dialogRef = this.dialog.open(DialogUpdateDeviceComponent, {
      width: '650px',
      maxWidth: '95vw',
      data: {
        device: deviceResumeDTO
      }
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.deviceService.notificarAlteracao();
      }
    });
  }

  adicionarSensor(device: DeviceResumeDTO): void {

    const dialogRef = this.dialogAdicionarSensor.open(
      DialogRegisterSensorComponent,
      {
        width: '650px',
        maxWidth: '95vw',

        data: {
          idDevice: device.id_device
        }
      }
    );

    dialogRef.afterClosed()
      .subscribe(resultado => {

        if (resultado === true) {

          this.deviceService
            .notificarAlteracao();
        }

      });

  }

  visualizarDispositivo(idDevice: number): void {
    this.router.navigate(['/devices', idDevice, 'telemetry']);
  }

  excluirDispositivo(idDevice: number): void {

    this.deviceService.excluirDispositivo(idDevice)
      .pipe(
        tap((response: any) => {
          this.toastService.success(response.descricao || 'Dispositivo excluído com sucesso.');
          this.deviceService.notificarAlteracao();
        }),
        catchError((error: any) => {
          this.toastService.error(error.descricao || 'Erro ao excluir dispositivo.');
          return of(error);
        })
      )
      .subscribe();
  }
}
