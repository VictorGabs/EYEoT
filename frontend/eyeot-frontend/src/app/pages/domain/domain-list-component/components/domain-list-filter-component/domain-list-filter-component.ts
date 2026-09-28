import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatAnchor, MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ToastService } from '../../../../../core/services/toast-service/toast-service';
import { DomainFilterDTO } from '../../../../../core/model/dtos/domain-filter.dto';

@Component({
  selector: 'app-domain-list-filter-component',
  imports: [MatCardModule, MatExpansionModule,MatButtonModule, MatFormFieldModule, MatSelectModule, MatInputModule, MatIconModule, MatAnchor, ReactiveFormsModule],
  templateUrl: './domain-list-filter-component.html',
  styleUrl: './domain-list-filter-component.scss',
})
export class DomainListFilterComponent {

  constructor(private toastService:ToastService){}

  @Output() filtrar = new EventEmitter<DomainFilterDTO>();

  filtro: DomainFilterDTO = {};

  readonly panelOpenState = signal(false);

  form = new FormGroup({
      busca: new FormControl('')
  })


  aplicarFiltro() {
    const value = this.form.getRawValue();

    const filtro: DomainFilterDTO = {
      busca: value.busca || undefined
    };

    this.filtrar.emit(filtro);
    console.log(filtro)
    this.toastService.success('Filtros aplicados com sucesso.');

  }

  limparFiltros() {

    this.form.reset({
      busca: ''
    });


    this.filtrar.emit({});

    this.toastService.success('Filtros limpos com sucesso.');
  }



}
