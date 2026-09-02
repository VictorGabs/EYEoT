import { Component } from '@angular/core';
import { ToastComponent } from "../../../../shared/toast-component/toast-component";
import { MatCardModule } from '@angular/material/card';
import { FormControl, FormGroup, FormGroupDirective, FormsModule, NgForm, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PersistenceService } from '../../../../core/services/persistence-service/persistence-service';
import { AuthService } from '../../../../core/services/auth-service/auth-service';
import { ToastService } from '../../../../core/services/toast-service/toast-service';
import { ErrorStateMatcher } from '@angular/material/core';
import { catchError, of, tap } from 'rxjs';
import { UserFormDTO } from '../../../../core/model/dtos/user-form.dto';


export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-register-form-component',
  imports: [MatCardModule,FormsModule, MatButtonModule, MatInputModule, MatIconModule, MatFormFieldModule, CommonModule, ReactiveFormsModule, ToastComponent],
  templateUrl: './register-form-component.html',
  styleUrl: './register-form-component.scss',
})

export class RegisterFormComponent {

  constructor(private router:Router, private persistenceService:PersistenceService, private authService:AuthService, private toast:ToastService){}

  hide = true;

  nameFormControl = new FormControl('', [Validators.required]);
  phoneFormControl = new FormControl('')
  email1FormControl = new FormControl('', [Validators.required, Validators.email]);
  email2FormControl = new FormControl('', [Validators.email]);
  passwordFormControl = new FormControl('', [Validators.required, Validators.minLength(8)]);

  form = new FormGroup({
    name: this.nameFormControl,
    phone: this.phoneFormControl,
    email1: this.email1FormControl,
    email2: this.email2FormControl,
    password: this.passwordFormControl
  });



  matcher = new MyErrorStateMatcher();

  onSubmit(){
    if (this.form.invalid){ this.toast.error("Preencha corretamente o formulário"); return};

    const dto: UserFormDTO = this.form.getRawValue() as UserFormDTO;

    this.persistenceService.postRequest(`/v1/users/cadastro`, dto)
      .pipe(
        tap((response: any) => {
          this.toast.success(response.descricao);
          setTimeout(() => {
            this.router.navigate(['login']);
          }, 2000)
        }),
        catchError((data: any) => {
          this.toast.error(data?.error?.descricao);
          return of(data);
        })
      )
      .subscribe()
  }
}
