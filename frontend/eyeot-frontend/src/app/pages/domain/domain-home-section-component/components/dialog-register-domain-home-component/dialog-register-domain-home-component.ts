import { PersistenceService } from './../../../../../core/services/persistence-service/persistence-service';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MyErrorStateMatcher } from '../../../../../core/validators/form-validator';
import { DomainDTO } from '../../../../../core/model/dtos/domain.dto';
import { ToastService } from '../../../../../core/services/toast-service/toast-service';
import { catchError, of, tap } from 'rxjs';
import { DomainFormDTO } from '../../../../../core/model/dtos/domain-form.dto';
import { AuthService } from '../../../../../core/services/auth-service/auth-service';



@Component({
  selector: 'app-dialog-register-domain-home-component',
  imports: [ ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        CommonModule,
        MatButtonModule],
  templateUrl: './dialog-register-domain-home-component.html',
  styleUrl: './dialog-register-domain-home-component.scss',
})
export class DialogRegisterDomainHomeComponent {

  constructor(private dialogRef:MatDialogRef<DialogRegisterDomainHomeComponent>, private persistenceService:PersistenceService, private toastService:ToastService){}

   matcher = new MyErrorStateMatcher();

   form = new FormGroup({
    domainName:new FormControl('', Validators.required),
    description:new FormControl('', Validators.required)
   });


    salvar(){

      if(this.form.invalid){
          this.form.markAllAsTouched();
          this.toastService.error("Formulário inválido");
          return;
      }
      const dto: DomainFormDTO = this.formToDTO();
      this.persistenceService.postRequest(`/v1/domains/register`, dto)
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
        domainName: value.domainName!,
        description: value.description!
      }
    }
}
