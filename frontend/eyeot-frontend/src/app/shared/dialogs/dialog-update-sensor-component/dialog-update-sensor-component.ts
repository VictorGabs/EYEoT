import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { SensorDialogData } from '../../../core/model/interfaces/sensorDialogData';
import { DeviceService } from '../../../core/services/device-service/device-service';
import { SensorTypeService } from '../../../core/services/sensor-type-service/sensor-type-service';
import { SensorService } from '../../../core/services/sensor-service/sensor-service';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { DeviceResumeDTO } from '../../../core/model/dtos/device-resume.dto';
import { SensorTypeDTO } from '../../../core/model/dtos/sensor-type.dto';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { SensorDTO } from '../../../core/model/dtos/sensor.dto';
import { SensorResumeDTO } from '../../../core/model/dtos/sensor-resume.dto';

interface EditSensorDialogData {
  sensor: SensorResumeDTO;
}

@Component({
  selector: 'app-dialog-update-sensor-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon],
  templateUrl: './dialog-update-sensor-component.html',
  styleUrl: './dialog-update-sensor-component.scss',
})
export class DialogUpdateSensorComponent {

private fb = inject(FormBuilder);

  private dialogRef =
    inject(MatDialogRef<DialogUpdateSensorComponent>);

  readonly data = inject<EditSensorDialogData>(MAT_DIALOG_DATA);

  readonly form = this.fb.group({
    device: [0, Validators.required],
    index: [0, Validators.required],
    sensorType: [null as number | null, Validators.required]
  });

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



  constructor(private persistenceService: PersistenceService) {

    this.carregarDispositivos();

    this.carregarSensorTypes();


    if (this.data.sensor.id_device) {

      this.form.controls.device.setValue(
        this.data.sensor.id_device
      );

    }

  }

  ngOnInit(): void {
    const sensor = this.data.sensor;

    this.form.patchValue({
      device: this.data.sensor.id_device,
      index: sensor.index,
      sensorType: sensor.id_sensortype
    });

    this.form.controls.device.disable();
    this.form.controls.index.disable();
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
      .atualizarSensor(dto, value.device!, value.index!)
      .subscribe({

        next: (response: any) => {

          this.toastService.success(
            response.descricao ??
            'Sensor atualizado com sucesso.'
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
