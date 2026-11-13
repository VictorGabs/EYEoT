
import { HttpInterceptorFn } from '@angular/common/http';

export const csrfInterceptor: HttpInterceptorFn = (req, next) => {
  const baseUrl = 'http://localhost:8080';

  const isApiRequest = req.url.startsWith(baseUrl);
  const safeMethods = ['GET', 'HEAD', 'OPTIONS'];

  if (!isApiRequest || safeMethods.includes(req.method.toUpperCase())) {
    return next(req);
  }

  const csrfCookie = document.cookie
    .split('; ')
    .find(cookie => cookie.startsWith('XSRF-TOKEN='));

  if (!csrfCookie) {
    console.warn('Cookie XSRF-TOKEN não encontrado.');
    return next(req);
  }

  const token = decodeURIComponent(
    csrfCookie.substring('XSRF-TOKEN='.length)
  );

  return next(req.clone({
    setHeaders: {
      'X-XSRF-TOKEN': token
    }
  }));
};
