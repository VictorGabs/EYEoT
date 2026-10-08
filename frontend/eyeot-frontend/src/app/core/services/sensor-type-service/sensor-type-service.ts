import { Injectable, signal } from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { SensorTypeFilterDTO } from '../../model/dtos/sensor-type-filter.dto';
import { SensorTypeDTO } from '../../model/dtos/sensor-type.dto';

@Injectable({
  providedIn: 'root'
})
export class SensorTypeService {

  constructor(private persistenceService: PersistenceService) {}

  private sensorTypeAlteradoSignal = signal(0);

  readonly sensorTypeAlterado = this.sensorTypeAlteradoSignal.asReadonly();


  listarTiposDeSensores(
      page: number,
      size: number,
      filtro?: SensorTypeFilterDTO
    ) {

      const params: any = {
        page,
        size
      };

      if (filtro?.busca) {
        params.busca = filtro.busca;
      }

      return this.persistenceService.getRequest(
        '/v1/sensor-types',
        { params }
      );
    }

    listarTodosTiposDeSensores() {
      return this.persistenceService.getRequest(
        '/v1/sensor-types/all'
      );
    }

    cadastrarTipoDeSensor(dto: SensorTypeDTO) {
      return this.persistenceService.postRequest(
        '/v1/sensor-types/register',
        dto
      );
    }

    notificarAlteracao(): void {
      this.sensorTypeAlteradoSignal.update(
        valor => valor + 1
      );
    }
}
