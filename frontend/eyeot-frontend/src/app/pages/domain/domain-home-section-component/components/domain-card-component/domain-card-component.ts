import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { DomainCardData } from '../../../../../core/model/interfaces/domain-card-data';

@Component({
  imports: [RouterLink,
    MatIconModule],
  selector: 'app-domain-card-component',
  styleUrl: './domain-card-component.scss',
  templateUrl: './domain-card-component.html',
})
export class DomainCardComponent {

  @Input({ required: true })
  domain!: DomainCardData;

}
