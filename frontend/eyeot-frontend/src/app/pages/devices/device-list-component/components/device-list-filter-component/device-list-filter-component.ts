import {
  Component,
  EventEmitter,
  Input,
  Output,
  signal
} from '@angular/core';

import {
  FormControl,
  FormGroup,
  ReactiveFormsModule
} from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { ToastService } from '../../../../../core/services/toast-service/toast-service';
import { DomainService } from '../../../../../core/services/domain-service/domain-service';

import { DomainResumeDTO } from '../../../../../core/model/dtos/domain-resume.dto';
import { DeviceFilterDTO } from '../../../../../core/model/dtos/device-filter.dto';

@Component({
  selector: 'app-device-list-filter-component',
  imports: [
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    ReactiveFormsModule
  ],
  templateUrl: './device-list-filter-component.html',
  styleUrl: './device-list-filter-component.scss',
})
export class DeviceListFilterComponent {

  constructor(
    private toastService: ToastService,
    private domainService: DomainService,
    private route: ActivatedRoute,
    private router: Router
  ) {

    this.carregarDominios();

  }

  @Output()
  filtrar = new EventEmitter<DeviceFilterDTO>();

  @Input()
  filtroInicial: DeviceFilterDTO = {};

  readonly panelOpenState = signal(false);

  readonly domains = signal<DomainResumeDTO[]>([]);

  form = new FormGroup({

    busca: new FormControl<string>(''),

    domain: new FormControl<number | null>(null)

  });

  ngOnInit(): void {

  if (this.filtroInicial.idDomain) {

    this.form.patchValue({
      domain: this.filtroInicial.idDomain
    });

  }

}



 carregarDominios(): void {

    this.domainService
      .listarTodosMeusDominios()
      .subscribe({

        next: (response: any) => {

          this.domains.set(
            response.data ?? []
          );

        },

        error: (error) => {

          console.error(
            'Erro ao carregar domínios:',
            error
          );

          this.toastService.error(
            'Não foi possível carregar os domínios.'
          );

        }

      });

  }


  aplicarFiltro(): void {

    const value =
      this.form.getRawValue();

    const filtro: DeviceFilterDTO = {

      busca:
        value.busca || undefined,

      idDomain:
        value.domain ?? undefined

    };

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        idDomain: null
      },
      queryParamsHandling: 'merge'
    });


    this.filtrar.emit(filtro);

    this.toastService.success(
      'Filtros aplicados com sucesso.'
    );

  }


  limparFiltros(): void {

    this.form.reset({
      busca: '',
      domain: null
    });

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        idDomain: null
      },
      queryParamsHandling: 'merge'
    });


    this.filtrar.emit({});

    this.toastService.success(
      'Filtros limpos com sucesso.'
    );

  }

}
