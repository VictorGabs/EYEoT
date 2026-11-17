import { PersistenceService } from './../../../core/services/persistence-service/persistence-service';
import { Component, effect, inject, signal } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { DomainResumeDTO } from '../../../core/model/dtos/domain-resume.dto';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { catchError, of, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogRegisterDomainHomeComponent } from '../../../shared/dialogs/dialog-register-domain-home-component/dialog-register-domain-home-component';
import { Router, RouterLink } from '@angular/router';
import { DomainService } from '../../../core/services/domain-service/domain-service';
import { AuthService } from '../../../core/services/auth-service/auth-service';
import { DomainCardComponent } from './components/domain-card-component/domain-card-component';




@Component({
  selector: 'app-domain-home-section-component',
  imports: [MatCardModule,MatDividerModule, MatProgressBarModule, MatButtonModule, MatIconModule, RouterLink, DomainCardComponent],
  templateUrl: './domain-home-section-component.html',
  styleUrl: './domain-home-section-component.scss',
})
export class DomainHomeSectionComponent {

  readonly dialog = inject (MatDialog);

  isAdmin = signal(false);


  constructor(private domainService: DomainService, private router:Router, private authService:AuthService){
      effect(() => {

        this.domainService.domainAlterado();

        this.carregarDominios();

      });

      this.authService.isAdmin().subscribe(isAdmin => {
        this.isAdmin.set(isAdmin);
      });
  }

  domains = signal<DomainResumeDTO[]>([]);

  carregando = signal(true);

  carregarDominios(){
     this.domainService.listarTodosMeusDominios()
          .pipe(
            tap((response: any) => {
              this.domains.set(response.data);
              this.carregando.set(false);
            }),
            catchError((data: any) => {
              this.carregando.set(false);
              return of(data);
            })
          )
          .subscribe();
  }

  abrirDialogRegisterDomainHome(){
    const dialogRef = this.dialog.open(DialogRegisterDomainHomeComponent)
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado === true) {
        this.carregando.set(true);
        this.domainService.notificarAlteracao();
      }
    });
  }

}
