import { Injectable, signal} from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { DomainFilterDTO } from '../../model/dtos/domain-filter.dto';
import { DomainFormDTO } from '../../model/dtos/domain-form.dto';


@Injectable({
  providedIn: 'root'
})
export class DomainService {

  constructor(private persistenceService:PersistenceService){}

  private domainAlteradoSignal = signal(0);

  readonly domainAlterado = this.domainAlteradoSignal.asReadonly();

  listarMeusDominios(
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

    listarTodosMeusDominios() {
      return this.persistenceService.getRequest(
        '/v1/domains/my/all'
      );
    }

    atualizarDominio(domainId: number, domainData: DomainFormDTO) {
      return this.persistenceService.putRequest(
        `/v1/domains/update/${domainId}`,
        domainData
      );
    }
    deletarDominio(domainId: number) {
      return this.persistenceService.deleteRequest(
        `/v1/domains/delete/${domainId}`
      );
    }

}
