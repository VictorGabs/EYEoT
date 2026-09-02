import { Service, signal } from '@angular/core';
import { Toast } from '../../model/toast-model';

@Service()
export class ToastService {

   readonly toast = signal<Toast | null>(null);

  success(message: string) {
    this.show(message, 'success');
  }

  error(message: string) {
    this.show(message, 'error');
  }

  private show(message: string, type: 'success' | 'error') {

    this.toast.set({
      message,
      type
    });

    setTimeout(() => this.toast.set(null), 4000);

  }
}
