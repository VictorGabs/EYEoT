import { Injectable, signal} from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { DomainFilterDTO } from '../../model/dtos/domain-filter.dto';


@Injectable({
  providedIn: 'root'
})
export class DomainService {

  constructor(private persistenceService:PersistenceService){}

  private domainAlteradoSignal = signal(0);

  readonly domainAlterado = this.domainAlteradoSignal.asReadonly();

  listarDominios(
      page: number,
      size: number,
      filtro?: DomainFilterDTO
    ) {

      const params: any = {
        page,
        size
      };

      if (filtro?.busca) {
        params.busca = filtro.busca;
      }


      return this.persistenceService.getRequest(
        '/v1/domains/my',
        { params }
      );
    }

    notificarAlteracao(): void {
      this.domainAlteradoSignal.update(valor => valor + 1);
    }

    listarDominiosResumo() {
      return this.persistenceService.getRequest(
        '/v1/domains/my/resume'
      );
    }

}
