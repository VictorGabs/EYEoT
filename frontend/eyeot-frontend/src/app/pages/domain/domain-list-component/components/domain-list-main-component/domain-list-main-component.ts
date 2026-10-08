import { Component, effect, Input, input, signal, SimpleChanges, ViewChild } from '@angular/core';
import { DomainResumeDTO } from '../../../../../core/model/dtos/domain-resume.dto';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { DomainFilterDTO } from '../../../../../core/model/dtos/domain-filter.dto';
import { DomainService } from '../../../../../core/services/domain-service/domain-service';
import { catchError, of, tap } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-domain-list-main-component',
  imports: [MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    RouterModule],
  templateUrl: './domain-list-main-component.html',
  styleUrl: './domain-list-main-component.scss',
})
export class DomainListMainComponent {

  constructor(private domainService:DomainService){
    effect(() => {

      this.domainService.domainAlterado();

      this.carregarDominios();

    });
  }

  domains = signal<DomainResumeDTO[]>([]);

  carregando = signal(true);

  pageNumber = 0;
  pageSize = 10;
  totalElements = 0;

  @Input()
  filtro: DomainFilterDTO = {};

  dataSource = new MatTableDataSource<DomainResumeDTO>();

  displayedColumns: string[] = [
    'domainName',
    'ownerName',
    'numDevices',
    'description',
    'actions'
  ];

   ngOnChanges(changes: SimpleChanges): void {

    if (changes['filtro']) {

      this.pageNumber = 0;

      this.carregarDominios();
    }
  }

    carregarDominios(){

      this.carregando.set(true);

       this.domainService.listarMeusDominios(
        this.pageNumber,
        this.pageSize,
        this.filtro)
            .pipe(
              tap((response: any) => {
                this.domains.set(response.data ?? []);
                this.totalElements = response.totalElements ?? 0;
                this.pageNumber = response.pageNumber ?? 0;
                this.pageSize = response.pageSize ?? this.pageSize;
                this.dataSource.data = this.domains();
                this.carregando.set(false);
              }),
              catchError((data: any) => {

                console.error('Erro ao carregar domínios:', data);

                this.domains.set([]);
                this.totalElements = 0;
                this.carregando.set(false);
                return of(data);
              })
            )
            .subscribe();
    }

  visualizarDominio(domain: DomainResumeDTO): void {
    console.log('Visualizar domínio:', domain);
  }

  mudouPagina(event: PageEvent) {

    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;

    this.carregarDominios();

  }



}
