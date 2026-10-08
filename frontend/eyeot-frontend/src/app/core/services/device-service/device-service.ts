import { Injectable, Service, signal } from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { DeviceFilterDTO } from '../../model/dtos/device-filter.dto';
import { DeviceDTO } from '../../model/dtos/device.dto';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(
    private persistenceService: PersistenceService
  ) {}

  private deviceAlteradoSignal = signal(0);

  readonly deviceAlterado =
    this.deviceAlteradoSignal.asReadonly();

  listarMeusDispositivos(
    page: number,
    size: number,
    filtro?: DeviceFilterDTO
  ) {

    const params: any = {
      page,
      size
    };

    if (filtro?.busca) {
      params.busca = filtro.busca;
    }

    if (filtro?.idDomain) {
      params.idDomain = filtro.idDomain;
    }

    return this.persistenceService.getRequest(
      '/v1/devices/my',
      { params }
    );
  }

  listarTodosMeusDispositivos() {
    return this.persistenceService.getRequest(
      '/v1/devices/my/all'
    );
  }

  cadastrarDispositivo(dto: DeviceDTO) {
    return this.persistenceService.postRequest(
      '/v1/devices/register',
      dto
    );
  }

  notificarAlteracao(): void {
    this.deviceAlteradoSignal.update(
      valor => valor + 1
    );
  }
}
