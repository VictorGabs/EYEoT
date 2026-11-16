import { Injectable, signal } from "@angular/core";
import { PersistenceService } from "../persistence-service/persistence-service";
import { catchError, map, Observable, of, tap } from "rxjs";
import { UserDTO } from "../../model/dtos/user.dto";
import { RolesEnum } from "../../enuns/roles-enum";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  usuarioLogado = signal<UserDTO | null>(null);

  constructor(private persistenceService:PersistenceService) {}

  isAuthenticated(): Observable<boolean> {
    return this.persistenceService.getRequest('/v1/auth/validate').pipe(
      map(() => true),
      catchError(() => of(false))
    )
  }

 logout(){
    this.usuarioLogado.set(null);

    this.persistenceService.getRequest('/v1/auth/logout').pipe(
      catchError(() => of(null))
    ).subscribe({
            complete: () => {
                window.location.href = '/login';
            }
        });

  }


  loadCurrentUser(): void {
    this.persistenceService.getRequest('/v1/auth/me')
    .pipe(
      tap((response:any)=>{
        this.usuarioLogado.set(response.data as UserDTO);
      }),catchError(error=>{
        this.usuarioLogado.set(null);
        return of(error);
      })
    )
    .subscribe();
  }

  isAdmin(): Observable<boolean> {

    return this.persistenceService
      .getRequest('/v1/auth/me')
      .pipe(
        map((response: any) => {

          const usuario =
            response.data as UserDTO;

          this.usuarioLogado.set(usuario);
          console.log('Usuario logado:', usuario);
          console.log('Papel do usuário:', usuario.role);
          console.log('É admin?', usuario.role === RolesEnum.ROLE_ADMIN);

          return usuario.role === RolesEnum.ROLE_ADMIN;
        }),

        catchError(() => {

          this.usuarioLogado.set(null);

          return of(false);
        })
      );
  }
}
