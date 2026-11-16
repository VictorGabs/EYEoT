import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login-component';
import { RegisterComponent } from './pages/register/register-component';
import { redirectAuthenticationGuard } from './core/guards/redirect-authentication-guard/redirect-authentication-guard';
import { HomeComponent } from './pages/home/home-component';
import { authGuard } from './core/guards/authentication-guard/auth-guard';
import { SidenavComponent } from './shared/layout/sidenav-component/sidenav-component';
import { DomainListComponent } from './pages/domain/domain-list-component/domain-list-component';
import { DeviceListComponent } from './pages/devices/device-list-component/device-list-component';
import { SensorsListComponent } from './pages/sensors/sensors-list-component/sensors-list-component';
import { SensorTypesListComponent } from './pages/sensor-types/sensor-types-list-component/sensor-types-list-component';
import { DeviceTelemetryComponent } from './pages/devices/device-telemetry-component/device-telemetry-component';
import { adminGuard } from './core/guards/admin-guard/admin-guard-guard';
import { UserListComponent } from './pages/user-list-component/user-list-component';

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
    path: '',
    component: SidenavComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'domains',
        component: DomainListComponent
      },
      {
        path: 'devices',
        component: DeviceListComponent
      },
      {
        path: 'sensors',
        component: SensorsListComponent
      },
      {
        path: 'sensor-types',
        component: SensorTypesListComponent,
        canActivate: [adminGuard],
      },
      {
        path: 'devices/:idDevice/telemetry',
        component: DeviceTelemetryComponent,
        canActivate: [authGuard]
      },
      {
         path: 'usuarios',
         component: UserListComponent,
         canActivate:[adminGuard]
     }

    ]
  }

];
