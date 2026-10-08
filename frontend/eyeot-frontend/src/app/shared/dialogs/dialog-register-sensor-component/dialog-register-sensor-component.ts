import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { SensorTypeDTO } from '../../../core/model/dtos/sensor-type.dto';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { MatIcon } from '@angular/material/icon';
import { DeviceService } from '../../../core/services/device-service/device-service';
import { SensorTypeService } from '../../../core/services/sensor-type-service/sensor-type-service';
import { SensorService } from '../../../core/services/sensor-service/sensor-service';
import { SensorDialogData } from '../../../core/model/interfaces/sensorDialogData';
import { DeviceResumeDTO } from '../../../core/model/dtos/device-resume.dto';
import { SensorDTO } from '../../../core/model/dtos/sensor.dto';

@Component({
  selector: 'app-dialog-register-sensor-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon],
  templateUrl: './dialog-register-sensor-component.html',
  styleUrl: './dialog-register-sensor-component.scss',
})
export class DialogRegisterSensorComponent {

private fb = inject(FormBuilder);

  private dialogRef =
    inject(MatDialogRef<DialogRegisterSensorComponent>);

  private data =
    inject(MAT_DIALOG_DATA, {
      optional: true
    }) as SensorDialogData | null;

  private deviceService =
    inject(DeviceService);

  private sensorTypeService =
    inject(SensorTypeService);

  private sensorService =
    inject(SensorService);

  private toastService =
    inject(ToastService);

  devices =
    signal<DeviceResumeDTO[]>([]);

  sensorTypes =
    signal<SensorTypeDTO[]>([]);

  selectedSensorType =
    signal<SensorTypeDTO | null>(null);

  form = this.fb.nonNullable.group({

    index: [
      0,
      [
        Validators.required,
        Validators.min(0)
      ]
    ],

     device: [
      null as number | null,
      [
        Validators.required
      ]
    ],

    sensorType: [
      null as number | null,
      [
        Validators.required
      ]
    ]

  });


  constructor(private persistenceService: PersistenceService) {

    this.carregarDispositivos();

    this.carregarSensorTypes();


    if (this.data?.idDevice) {

      this.form.controls.device.setValue(
        this.data.idDevice
      );

    }

  }

  carregarDispositivos(): void {

    this.deviceService
      .listarTodosMeusDispositivos()
      .subscribe({

        next: (response: any) => {

          this.devices.set(
            response.data ?? []
          );

        },

        error: (error: any) => {

          console.error(
            'Erro ao carregar dispositivos:',
            error
          );

          this.toastService.error(
            'Não foi possível carregar os dispositivos.'
          );

        }

      });

  }

  carregarSensorTypes(): void {

    this.sensorTypeService.listarTodosTiposDeSensores()
      .subscribe({

        next: (response: any) => {

          this.sensorTypes.set(
            response.data ?? []
          );

        },

        error: (error: any) => {

          console.error(
            'Erro ao carregar tipos de sensores:',
            error
          );

          this.toastService.error(
            'Não foi possível carregar os tipos de sensores.'
          );

        }

      });

  }


  sensorTypeSelecionado(
    id: number
  ): void {

    const sensorType =
      this.sensorTypes().find(
        type => type.id === id
      );

    this.selectedSensorType.set(
      sensorType ?? null
    );

  }

  salvar(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      this.toastService.error(
        'Preencha corretamente os campos obrigatórios.'
      );

      return;

    }


    const value =
      this.form.getRawValue();


    const dto: SensorDTO = {

      id_device:
        value.device!,

      index:
        value.index!,

      id_sensortype:
        value.sensorType!

    };


    this.sensorService
      .cadastrarSensor(dto)
      .subscribe({

        next: (response: any) => {

          this.toastService.success(
            response.descricao ??
            'Sensor cadastrado com sucesso.'
          );

          this.dialogRef.close(true);

        },

        error: (error: any) => {

          console.error(
            'Erro ao cadastrar sensor:',
            error
          );

          this.toastService.error(
            error?.error?.descricao ??
            'Erro ao cadastrar sensor.'
          );

        }

      });

  }


  cancelar(): void {

    this.dialogRef.close(false);

  }

}
