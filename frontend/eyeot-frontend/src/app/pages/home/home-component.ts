import { Component } from '@angular/core';
import { ToastComponent } from '../../shared/toast-component/toast-component';
import { DomainHomeSectionComponent } from '../domain/domain-home-section-component/domain-home-section-component';

@Component({
  selector: 'app-home-component',
  imports: [ToastComponent, DomainHomeSectionComponent],
  templateUrl: './home-component.html',
  styleUrl: './home-component.scss',
})
export class HomeComponent {}
