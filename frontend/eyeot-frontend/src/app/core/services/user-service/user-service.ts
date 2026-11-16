import { Injectable } from '@angular/core';
import { RolesEnum } from '../../enuns/roles-enum';
import { PersistenceService } from '../persistence-service/persistence-service';


@Injectable({
  providedIn:'root'
})
export class UserService {
  constructor(private persistenceService:PersistenceService){}

  listarUsuarios(
    page: number,
    size: number,
    busca?: string
  ){

    let url = `/v1/users?page=${page}&size=${size}`;


    if (busca?.trim()) {

      url +=
        `&busca=${encodeURIComponent(busca.trim())}`;

    }


    return this.persistenceService
      .getRequest(url);

  }


  alterarPapel(id: number, role: RolesEnum) {

    return this.persistenceService
      .putRequest(
        `/v1/users/${id}/papel`,
        { role }
      );

  }

}

