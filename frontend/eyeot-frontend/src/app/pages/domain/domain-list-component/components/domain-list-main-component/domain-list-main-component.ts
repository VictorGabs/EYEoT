import { Component, Input, input, signal, ViewChild } from '@angular/core';
import { DomainResumeDTO } from '../../../../../core/model/dtos/domain-resume.dto';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-domain-list-main-component',
  imports: [    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule],
  templateUrl: './domain-list-main-component.html',
  styleUrl: './domain-list-main-component.scss',
})
export class DomainListMainComponent {

  domains = input<DomainResumeDTO[]>([]);

  carregando = input(false);

  pageNumber = input(0);
  pageSize = input(10);
  totalElements = input(0);

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  dataSource = new MatTableDataSource<DomainResumeDTO>();

  displayedColumns: string[] = [
    'domainName',
    'ownerName',
    'numDevices',
    'description',
    'actions'
  ];

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  ngOnChanges(): void {
    this.dataSource.data = this.domains();

    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  visualizarDominio(domain: DomainResumeDTO): void {
    console.log('Visualizar domínio:', domain);
  }



}
