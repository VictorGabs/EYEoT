import { Component, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../header-component/header-component';
import { PersistenceService } from '../../../core/services/persistence-service/persistence-service';
import { catchError, of, tap } from 'rxjs';
import { TabDTO } from '../../../core/model/dtos/tab.dto';

@Component({
  selector: 'app-sidenav-component',
  imports: [ RouterOutlet,
    RouterLink,
    RouterLinkActive,

    MatSidenavModule,
    MatListModule,
    MatIconModule,

    HeaderComponent],
  templateUrl: './sidenav-component.html',
  styleUrl: './sidenav-component.scss',
})
export class SidenavComponent {
  constructor(private persistenceService:PersistenceService){}

  tabs = signal<TabDTO[]>([]);

  ngOnInit(){
    this.loadTabs();
  }

   private loadTabs() {
    this.persistenceService.getRequest('/v1/tab').pipe(tap((response: any) => {
      this.tabs.set(response.data);
    }), catchError(error => {
      this.tabs.set([]);
      return of(error);
    })).subscribe();
  }
}
