import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { PaginatorIntl } from './shared/layout/custom-paginator/custom-paginator';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { csrfInterceptor } from './core/interceptors/csrf.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    {
      provide: MatPaginatorIntl,
      useClass: PaginatorIntl
    },
    provideHttpClient(
      withInterceptors([csrfInterceptor])
    )
  ]
};
