import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogContent, MatDialogModule } from '@angular/material/dialog';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { DomainResumeDTO } from '../../../core/model/dtos/domain-resume.dto';
import { DomainService } from '../../../core/services/domain-service/domain-service';
import { DeviceFormDTO } from '../../../core/model/dtos/device-form.dto';
import { MatFormField, MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-dialog-register-device-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon, MatSlideToggleModule],
  templateUrl: './dialog-register-device-component.html',
  styleUrl: './dialog-register-device-component.scss',
})
export class DialogRegisterDeviceComponent {


  constructor() {
    this.carregarDominios();
  }

  private fb = inject(FormBuilder);
  private dialogRef =
    inject(MatDialogRef<DialogRegisterDeviceComponent>);

  private persistenceService =
    inject(PersistenceService);

  private toastService =
    inject(ToastService);

  domains = signal<DomainResumeDTO[]>([]);

  form = this.fb.nonNullable.group({

    id_domain: [
      0
    ],

    enable: [
      true
    ],

    latitude: [
      null as number | null
    ],

    longitude: [
      null as number | null
    ],

    altitude: [
      null as number | null
    ]

  });


  carregarDominios() {

    this.persistenceService
      .getRequest('/v1/domains/my/all')
      .subscribe((response: any) => {

        this.domains.set(
          response.data ?? []
        );

      });

  }

  salvar() {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;
    }

    const dto: DeviceFormDTO =
      this.form.getRawValue();

    this.persistenceService
      .postRequest(
        '/v1/devices/register',
        dto
      )
      .subscribe({

        next: (response: any) => {

          const deviceId = response.data;

          this.dialogRef.close({
            success: true,
            deviceId
          });

        },

        error: (error) => {

          this.toastService.error(
            error?.error?.descricao ??
            'Erro ao cadastrar dispositivo.'
          );

        }

      });

  }
}
