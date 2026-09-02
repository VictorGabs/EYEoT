import { Injectable, signal } from "@angular/core";
import { PersistenceService } from "../persistence-service/persistence-service";
import { catchError, map, Observable, of, tap } from "rxjs";
import { UserDTO } from "../../model/dtos/user.dto";

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
                window.location.href = '/interno/login';
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
}
