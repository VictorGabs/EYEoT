import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, FormGroupDirective, FormsModule, NgForm, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ToastComponent } from '../../../../shared/toast-component/toast-component';
import { ErrorStateMatcher } from '@angular/material/core';
import { Router } from '@angular/router';
import { PersistenceService } from '../../../../core/services/persistence-service/persistence-service';
import { ToastService } from '../../../../core/services/toast-service/toast-service';
import { AuthService } from '../../../../core/services/auth-service/auth-service';
import { catchError, of, tap } from 'rxjs';
import { LoginRequestDTO } from '../../../../core/model/dtos/login-request.dto';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-login-form-component',
  imports: [MatCardModule, FormsModule, MatButtonModule, MatInputModule, MatIconModule, MatFormFieldModule, CommonModule, ReactiveFormsModule, ToastComponent],
  templateUrl: './login-form-component.html',
  styleUrl: './login-form-component.scss',
})
export class LoginFormComponent {
    constructor(private router:Router, private persistenceService:PersistenceService, private authService:AuthService, private toast: ToastService){}

  hide = true;

  emailFormControl = new FormControl('', [Validators.required, Validators.email]);
  passwordFormControl = new FormControl('', [Validators.required]);

  form = new FormGroup({
    email: this.emailFormControl,
    password: this.passwordFormControl
  });

  matcher = new MyErrorStateMatcher();

  onSubmit(){
    if (this.form.invalid){ this.toast.error("Preencha corretamente o formulário"); return};

    const dto: LoginRequestDTO = this.form.getRawValue() as LoginRequestDTO;

    this.persistenceService.postRequest(`/v1/users/login`, dto)
      .pipe(
        tap((response: any) => {
          this.toast.success(response.descricao);
          setTimeout(() => {
            this.router.navigate(['home']);
          }, 2000)
          this.authService.loadCurrentUser();
        }),
        catchError((data: any) => {
          this.toast.error(data?.error?.descricao);
          return of(data);
        })
      )
      .subscribe()
  }
}
