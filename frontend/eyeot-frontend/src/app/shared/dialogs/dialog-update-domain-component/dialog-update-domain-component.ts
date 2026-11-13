import { PersistenceService } from './../../../core/services/persistence-service/persistence-service';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DomainResumeDTO } from '../../../core/model/dtos/domain-resume.dto';
import { DomainService } from '../../../core/services/domain-service/domain-service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MyErrorStateMatcher } from '../../../core/validators/form-validator';
import { DomainFormDTO } from '../../../core/model/dtos/domain-form.dto';
import { catchError, of, tap } from 'rxjs';
import { ToastService } from '../../../core/services/toast-service/toast-service';
import { UserResumeDTO } from '../../../core/model/dtos/user-resume.dto';
import { AuthService } from '../../../core/services/auth-service/auth-service';


interface EditDomainDialogData {
  domain: DomainResumeDTO;
}
@Component({
  selector: 'app-dialog-update-domain-component',
  imports: [ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        CommonModule,
        MatButtonModule],
  templateUrl: './dialog-update-domain-component.html',
  styleUrl: './dialog-update-domain-component.scss',
})
export class DialogUpdateDomainComponent {

  constructor(private toastService:ToastService, private persistenceService: PersistenceService, private authService:AuthService) {}

  isAdmin = signal(false);

  matcher = new MyErrorStateMatcher();

  private readonly dialogRef = inject(MatDialogRef<DialogUpdateDomainComponent>);

  private readonly domainService = inject(DomainService);


  users = signal<UserResumeDTO[]>([]);

  readonly data = inject<EditDomainDialogData>(MAT_DIALOG_DATA);

  form = new FormGroup({
    domainName:new FormControl('', Validators.required),
    description:new FormControl('', Validators.required),
    ownerId: new FormControl<number | null>(null)
  });


  ngOnInit(): void {

    const domain = this.data.domain;

    this.form.patchValue({
      domainName: domain.domainName,
      description: domain.description ?? '',
      ownerId: domain.id_owner
    });


  this.authService.isAdmin().subscribe(isAdmin => {

    this.isAdmin.set(isAdmin);

    if (isAdmin) {
      this.carregarUsuarios();
    }
  });
}

  private carregarUsuarios(): void {
    this.persistenceService.getRequest('/v1/users/all/resume').subscribe({
      next: (response: any) => {
        this.users.set(response.data ?? []);
      },
      error: () => {
        this.toastService.error('Não foi possível carregar os usuários.');
      }
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.domainService
      .atualizarDominio(
        this.data.domain.id_domain,
        this.formToDTO()
      )
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
        .subscribe()

  }

  private formToDTO(): DomainFormDTO {
    const value = this.form.getRawValue();

    return {
      id_domain: this.data.domain.id_domain,
      domainName: value.domainName!,
      description: value.description!,
      ...(this.isAdmin() ? { id_owner: value.ownerId! } : {})
    };
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
