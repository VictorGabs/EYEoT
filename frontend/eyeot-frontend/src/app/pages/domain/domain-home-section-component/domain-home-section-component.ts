import { PersistenceService } from './../../../core/services/persistence-service/persistence-service';
import { Component, inject, signal } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { DomainResumeDTO } from '../../../core/model/dtos/domain-resume.dto';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { ErrorStateMatcher } from '@angular/material/core';
import { FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { catchError, of, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { DialogRegisterDomainHomeComponent } from './components/dialog-register-domain-home-component/dialog-register-domain-home-component';




@Component({
  selector: 'app-domain-home-section-component',
  imports: [MatCardModule,MatDividerModule, MatProgressBarModule, MatButtonModule, MatIconModule],
  templateUrl: './domain-home-section-component.html',
  styleUrl: './domain-home-section-component.scss',
})
export class DomainHomeSectionComponent {

  readonly dialog = inject (MatDialog);


  constructor(private persistenceService:PersistenceService){
    this.carregarDominios();
  }

  domains = signal<DomainResumeDTO[]>([]);

  carregando = signal(true);

  carregarDominios(){
     this.persistenceService.getRequest(`/v1/domains/my/resume`)
          .pipe(
            tap((response: any) => {
              this.domains.set(response.data);
              this.carregando.set(false);
            }),
            catchError((data: any) => {
              return of(data);
            })
          )
          .subscribe();
  }

  abrirDialogRegisterDomainHome(){
    const dialogRef = this.dialog.open(DialogRegisterDomainHomeComponent)
    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado?.sucesso) {
        this.carregando.set(true);
        this.carregarDominios();
      }
    });
  }


}
