import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
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
import { SENSOR_DATA_TYPE_OPTIONS, SensorTypeDataTypeEnum } from '../../../core/enuns/sensor-type-data-type.enum';

interface EditSensorTypeDialogData {
  sensorType: SensorTypeDTO;
}

@Component({
  selector: 'app-dialog-update-sensor-type-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon],
  templateUrl: './dialog-update-sensor-type-component.html',
  styleUrl: './dialog-update-sensor-type-component.scss',
})
export class DialogUpdateSensorTypeComponent {

   private fb = inject(FormBuilder);

  private dialogRef =
    inject(MatDialogRef<DialogUpdateSensorTypeComponent>);

  private persistenceService =
    inject(PersistenceService);

  private toastService =
    inject(ToastService);

  readonly dataTypes = SENSOR_DATA_TYPE_OPTIONS;

  readonly data = inject<EditSensorTypeDialogData>(MAT_DIALOG_DATA);

  private sensorType = this.data.sensorType;

  form = this.fb.group({

    description: [
      '',
      [
        Validators.required,
        Validators.maxLength(256)
      ]
    ],

    dataType: [
      null as SensorTypeDataTypeEnum | null,
      [
        Validators.required
      ]
    ],

    minValue: [
      null as number | null
    ],

    maxValue: [
      null as number | null
    ],

    physicalQuantity: [
      '',
      [
        Validators.required,
        Validators.maxLength(64)
      ]
    ],

    measureUnit: [
      '',
      [
        Validators.required,
        Validators.maxLength(12)
      ]
    ],

    averageInterval: [
      null as number | null
    ]
  });

  ngOnInit(): void {


    this.form.patchValue({
      description: this.sensorType.description,
      dataType: this.sensorType.dataType,
      minValue: this.sensorType.minValue,
      maxValue: this.sensorType.maxValue,
      physicalQuantity: this.sensorType.physicalQuantity,
      measureUnit: this.sensorType.measureUnit,
      averageInterval: this.sensorType.averageInterval
    });
  }

  salvar(): void {

    if (this.form.invalid) {
      this.form.markAllAsTouched();

      this.toastService.error(
        'Preencha corretamente os campos obrigatórios.'
      );

      return;
    }

    const value = this.form.getRawValue();

    const dto: SensorTypeDTO = {
      description: value.description!,
      dataType: value.dataType!,
      minValue: value.minValue,
      maxValue: value.maxValue,
      physicalQuantity: value.physicalQuantity!,
      measureUnit: value.measureUnit!,
      averageInterval: value.averageInterval
    };

    this.persistenceService
      .putRequest(`/v1/sensor-types/update/${this.sensorType.id}`, dto)
      .subscribe({

        next: (response: any) => {

          this.toastService.success(
            response.descricao
          );

          this.dialogRef.close(true);
        },

        error: (error: any) => {

          console.error(
            'Erro ao cadastrar tipo de sensor:',
            error
          );

          this.toastService.error(
            error?.error?.descricao ??
            'Erro ao cadastrar tipo de sensor.'
          );
        }

      });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
