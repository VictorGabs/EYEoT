import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login-component';
import { RegisterComponent } from './pages/register/register-component';
import { redirectAuthenticationGuard } from './core/guards/redirect-authentication-guard/redirect-authentication-guard';
import { HomeComponent } from './pages/home/home-component';
import { authGuard } from './core/guards/authentication-guard/auth-guard';

export const routes: Routes = [
  {
    path:'',
    redirectTo:'login',
    pathMatch: 'full'
  },

  {
    path: 'login',
    component: LoginComponent,
    canActivate: [redirectAuthenticationGuard]
  },
  {
    path:'cadastro',
    component: RegisterComponent,
    canActivate: [redirectAuthenticationGuard]
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard]
  }

];
