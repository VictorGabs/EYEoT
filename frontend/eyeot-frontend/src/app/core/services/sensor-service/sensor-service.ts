import { Injectable, signal } from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { SensorFilterDTO } from '../../model/dtos/sensor-filter.dto';
import { SensorDTO } from '../../model/dtos/sensor.dto';

@Injectable({
  providedIn: 'root'
})
export class SensorService {

  constructor(private persistenceService: PersistenceService) {}

  private sensorAlteradoSignal = signal(0);

  readonly sensorAlterado = this.sensorAlteradoSignal.asReadonly();


  listarMeusSensores(
      page: number,
      size: number,
      filtro?: SensorFilterDTO
    ) {

      const params: any = {
        page,
        size
      };

      if (filtro?.busca) {
        params.busca = filtro.busca;
      }

      return this.persistenceService.getRequest(
        '/v1/sensors/my',
        { params }
      );
    }


  listarTodosMeusSensores() {
      return this.persistenceService.getRequest(
        '/v1/sensors/my/all'
      );
    }

    cadastrarSensor(dto: SensorDTO) {
      return this.persistenceService.postRequest(
        '/v1/sensors/register',
        dto
      );
    }

    atualizarSensor(dto: SensorDTO, idDevice: number, index: number) {
      return this.persistenceService.putRequest(
        `/v1/sensors/update/${idDevice}/${index}`,
        dto
      );
    }

    deletarSensor(idDevice: number, index: number) {
      return this.persistenceService.deleteRequest(
        `/v1/sensors/delete/${idDevice}/${index}`
      );
    }

    notificarAlteracao(): void {
      this.sensorAlteradoSignal.update(
        valor => valor + 1
      );
    }

    listarSensoresDoDispositivo(idDevice: number) {
      return this.persistenceService.getRequest(
        `/v1/sensors/device/${idDevice}`
      );
    }
}
