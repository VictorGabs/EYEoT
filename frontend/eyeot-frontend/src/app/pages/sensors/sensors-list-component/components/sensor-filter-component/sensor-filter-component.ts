import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ToastService } from '../../../../../core/services/toast-service/toast-service';
import { SensorFilterDTO } from '../../../../../core/model/dtos/sensor-filter.dto';

@Component({
  selector: 'app-sensor-filter-component',
  imports: [MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    ReactiveFormsModule],
  templateUrl: './sensor-filter-component.html',
  styleUrl: './sensor-filter-component.scss',
})
export class SensorFilterComponent {

  constructor(private toastService:ToastService){}


  @Output() filtrar = new EventEmitter<SensorFilterDTO>();

  filtro: SensorFilterDTO = {};

  readonly panelOpenState = signal(false);

  form = new FormGroup({
      busca: new FormControl('')
  })


  aplicarFiltro() {
    const value = this.form.getRawValue();

    const filtro: SensorFilterDTO = {
      busca: value.busca || undefined
    };

    this.filtrar.emit(filtro);
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
