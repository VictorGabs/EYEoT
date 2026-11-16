import { RolesEnum } from './../../core/enuns/roles-enum';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {Component, OnInit, signal } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { catchError, finalize, of, tap } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import {MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { FormsModule} from '@angular/forms';
import {MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { UserDTO } from '../../core/model/dtos/user.dto';
import { ToastService } from '../../core/services/toast-service/toast-service';
import { UserService } from '../../core/services/user-service/user-service';
import { DialogConfirmarAlteracaoRoleComponent } from './components/dialog-confirmar-alteracao-role-component/dialog-confirmar-alteracao-role-component';
import { ToastComponent } from '../../shared/toast-component/toast-component';

@Component({
  imports: [
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatTableModule,
    MatIconModule,
    MatProgressBarModule,
    MatCardModule,
    MatPaginatorModule,
    FormsModule,
    MatInputModule,
    ToastComponent,
    MatButtonModule,
    MatSlideToggleModule
  ],
  selector: 'app-user-list-component',
  styleUrl: './user-list-component.scss',
  templateUrl: './user-list-component.html',
})
export class UserListComponent {  displayedColumns = [
    'nome',
    'email_principal',
    'email_secundario',
    'role',
    'phone',
    'actions'
  ];

  totalElementos = 0;

  pageSize = 10;

  pageIndex = 0;

  busca = '';

  dataSource = new MatTableDataSource<UserDTO>();

  carregando = signal(true);

  usuarios = signal<Array<UserDTO>>([]);

  readonly roles = Object.values(RolesEnum);

  papelVisual = new Map<number, RolesEnum>();

  constructor(
    private toastService: ToastService,
    private dialog:MatDialog,
    private usuarioService:UserService,
  ) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  private carregarUsuarios(): void {

    this.carregando.set(true);

   this.usuarioService.listarUsuarios(this.pageIndex,this.pageSize, this.busca).pipe(
      tap((response:any)=>{
        this.usuarios.set(response.data);
        this.dataSource.data = this.usuarios();
        this.totalElementos = response.totalElements;

      }),
      catchError(error => {
        this.usuarios.set([]);
        this.toastService.error(error.descricao);
        return of(error);
      }),
      finalize(()=>{this.carregando.set(false)})
    ).subscribe();
  }

    pesquisar(): void {

    this.pageIndex = 0;

    this.carregarUsuarios();

    this.toastService.success("Busca aplicada!");
  }


  limparPesquisa(): void {

    this.busca = '';

    this.pageIndex = 0;

    this.carregarUsuarios();

    this.toastService.success("Busca limpada!");

  }


  mudouPagina(event: PageEvent): void {

    this.pageIndex = event.pageIndex;

    this.pageSize = event.pageSize;

    this.carregarUsuarios();
  }


  alterarPapel(usuario: UserDTO,novoPapel: RolesEnum): void {

    const papelAnterior = this.papelVisual.get(usuario.id_user) ?? usuario.role;

    if (papelAnterior === novoPapel) {
      return;
    }

    const dialogRef = this.dialog.open(
      DialogConfirmarAlteracaoRoleComponent,
      {
        width: '420px',
        maxWidth: '90vw',

        data: {
          usuario,
          novoPapel
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmou => {

      if (!confirmou) {
        this.papelVisual.set(usuario.id_user, papelAnterior);
        return;
      }

      this.carregando.set(true);

      this.usuarioService
        .alterarPapel(usuario.id_user, novoPapel)
        .pipe(
          tap((response:any)=>{
            usuario.role = novoPapel;
            this.papelVisual.delete(usuario.id_user);
            this.toastService.success(response.descricao)
          }),
          catchError(error=>{
            usuario.role = papelAnterior;

            this.papelVisual.set(
              usuario.id_user,
              papelAnterior
            );

            this.toastService.error(error.descricao);
            return of(error);
          }),
          finalize(() => {
            this.carregando.set(false);
          })
        )
        .subscribe();
    });
  }



  getInicial(nome: string): string {

    if (!nome) {
      return '?';
    }

    return nome
      .trim()
      .charAt(0)
      .toUpperCase();
  }


  getRoleLabel(role: RolesEnum): string {
    if(role == RolesEnum.ROLE_ADMIN){
      return "Administrador"
    } else {
      return "Usuário"
    }

  }

}
