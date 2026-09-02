import { Component } from '@angular/core';
import { RegisterFormComponent } from "./components/register-form-component/register-form-component";

@Component({
  selector: 'app-register-component',
  imports: [RegisterFormComponent],
  templateUrl: './register-component.html',
  styleUrl: './register-component.scss',
})
export class RegisterComponent {}
