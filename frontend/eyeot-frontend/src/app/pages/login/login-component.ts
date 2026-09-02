import { Component } from '@angular/core';
import { LoginFormComponent } from "./components/login-form-component/login-form-component";

@Component({
  selector: 'app-login-component',
  imports: [LoginFormComponent],
  templateUrl: './login-component.html',
  styleUrl: './login-component.scss',
})
export class LoginComponent {}
