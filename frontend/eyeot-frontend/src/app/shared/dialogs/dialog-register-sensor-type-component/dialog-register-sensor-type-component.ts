import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { SensorTypeDTO } from '../../../core/model/dtos/sensor-type.dto';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { MatIcon } from '@angular/material/icon';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SENSOR_DATA_TYPE_OPTIONS, SensorTypeDataTypeEnum } from '../../../core/enuns/sensor-type-data-type.enum';

@Component({
  selector: 'app-dialog-register-sensor-type-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon],
  templateUrl: './dialog-register-sensor-type-component.html',
  styleUrl: './dialog-register-sensor-type-component.scss',
})
export class DialogRegisterSensorTypeComponent {

  private fb = inject(FormBuilder);

  private dialogRef =
    inject(MatDialogRef<DialogRegisterSensorTypeComponent>);

  private persistenceService =
    inject(PersistenceService);

  private toastService =
    inject(ToastService);

  private destroyRef = inject(DestroyRef);

  readonly dataTypes = SENSOR_DATA_TYPE_OPTIONS;

  readonly summaryPeriods = [
    { value: 30, label: '30 minutos' },
    { value: 60, label: '1 hora' },
    { value: 720, label: '12 horas' },
    { value: 1440, label: '24 horas' }
  ];

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
    ],

    retentionPeriod: [
      null as number | null,
      [
        Validators.min(1),
        Validators.pattern(/^\d+$/)
      ]
    ],
    summaryPeriod: [
      {
        value: null as number | null,
        disabled: true
      },
      [
        Validators.required
      ]
    ]

  });

  constructor() {
    this.form.controls.dataType.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(dataType => {
        this.atualizarCamposPorTipo(dataType);
      });
  }

  private atualizarCamposPorTipo(
    dataType: SensorTypeDataTypeEnum | null
  ): void {
    const minValue = this.form.controls.minValue;
    const maxValue = this.form.controls.maxValue;
    const summaryPeriod = this.form.controls.summaryPeriod;

    if (dataType === 1 || dataType === 5) {
      summaryPeriod.enable();
    } else {
      summaryPeriod.setValue(null);
      summaryPeriod.disable();
    }

    if (dataType === 2) {
      minValue.setValue(0);
      maxValue.setValue(1);

      minValue.disable();
      maxValue.disable();

      return;
    }

    if (dataType === 3) {
      minValue.setValue(null);
      maxValue.setValue(null);

      minValue.disable();
      maxValue.disable();

      return;
    }

    // Demais tipos: campos disponíveis.
    minValue.enable();
    maxValue.enable();
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
      averageInterval: value.averageInterval,
      retentionPeriod: value.retentionPeriod,
      summaryPeriod: value.summaryPeriod
    };

    this.persistenceService
      .postRequest('/v1/sensor-types/register', dto)
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
