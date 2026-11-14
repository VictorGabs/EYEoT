import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { DomainResumeDTO } from '../../../core/model/dtos/domain-resume.dto';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { DeviceResumeDTO } from '../../../core/model/dtos/device-resume.dto';
import { catchError } from 'rxjs/internal/operators/catchError';
import { of, tap } from 'rxjs';

interface EditDeviceDialogData {
  device: DeviceResumeDTO;
}

@Component({
  selector: 'app-dialog-update-device-component',
  imports: [ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule, MatIcon, MatSlideToggleModule],
  templateUrl: './dialog-update-device-component.html',
  styleUrl: './dialog-update-device-component.scss',
})
export class DialogUpdateDeviceComponent {

  domains = signal<DomainResumeDTO[]>([]);

  private fb = inject(FormBuilder);
  private dialogRef =
    inject(MatDialogRef<DialogUpdateDeviceComponent>);

  constructor(private persistenceService: PersistenceService, private toastService: ToastService) {
    this.carregarDominios();
  }
  readonly data = inject<EditDeviceDialogData>(MAT_DIALOG_DATA);

  readonly form = this.fb.group({
    id_domain: [null as number | null, Validators.required],
    enable: [true],
    latitude: [
      null as number | null,
      [
        Validators.min(-90),
        Validators.max(90),
        Validators.pattern(/^-?\d{1,2}(\.\d{1,6})?$/)
      ]
    ],

    longitude: [
      null as number | null,
      [
        Validators.min(-180),
        Validators.max(180),
        Validators.pattern(/^-?\d{1,3}(\.\d{1,6})?$/)
      ]
    ],

    altitude: [
      null as number | null,
      [
        Validators.min(-9999.99),
        Validators.max(9999.99),
        Validators.pattern(/^-?\d{1,4}(\.\d{1,2})?$/)
      ]
    ]
  });

  ngOnInit(): void {
    const device = this.data.device;

    this.form.patchValue({
      id_domain: device.id_domain,
      enable: device.enable,
      latitude: device.latitude,
      longitude: device.longitude,
      altitude: device.altitude
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.persistenceService.putRequest(`/v1/devices/update/${this.data.device.id_device}`, this.form.getRawValue())
      .pipe(
        tap((response: any) => {
          this.toastService.success(response.descricao);
          this.dialogRef.close(true);
        }),
        catchError((data: any) => {
          this.toastService.error(data?.error?.descricao);
          return of(data);
        })
      )
      .subscribe();
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
  carregarDominios() {

    this.persistenceService
      .getRequest('/v1/domains/my/all')
      .subscribe((response: any) => {

        this.domains.set(
          response.data ?? []
        );

      });

  }
}
