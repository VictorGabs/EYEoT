import { PersistenceService } from './../persistence-service/persistence-service';
import { Injectable, OnDestroy, Service } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';
import { AuthService } from '../auth-service/auth-service';

export interface TelemetryChangedEventDTO {
  idTelemetryData: number;
  deviceId: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class TelemetryWebsocketService implements OnDestroy{

  constructor(private persistenceService:PersistenceService, private authService:AuthService){}

  private client?: Client;

  private readonly telemetryChangedSubject =
    new Subject<TelemetryChangedEventDTO>();

  readonly telemetryChanged$: Observable<TelemetryChangedEventDTO> =
    this.telemetryChangedSubject.asObservable();



 connect(): void {

    if(this.client?.active){
      return
    }

    this.persistenceService.getRequest('/v1/auth/csrf')
      .subscribe({
        next: (csrf:any) => {
          this.client = new Client({
            webSocketFactory: () =>
              new SockJS('http://localhost:8080/ws'),

            connectHeaders: {
              [csrf.headerName]: csrf.token
            },

            reconnectDelay: 5000,

            onConnect: () => {
              console.log('STOMP conectado!');

              const subscription = this.client?.subscribe(
                '/user/queue/telemetry/changed',
                (message) => this.processarNotificacao(message)
              );

                this.authService.isAdmin().subscribe({
                next: (isAdmin) => {
                  if (isAdmin) {
                    this.client?.subscribe(
                      '/topic/admin/telemetry/changed',
                      (message) => this.processarNotificacao(message)
                    );
                  }
                },
                error: (erro) => {
                  console.error('Erro ao verificar perfil:', erro);
                }
              });

              console.log('Inscrição realizada:', subscription);
            },

            onStompError: (frame) => {
              console.error('Erro STOMP:', frame.headers);
              console.error('Detalhes:', frame.body);
            },

            onWebSocketClose: (event) => {
              console.log(
                'WebSocket fechado:',
                event.code,
                event.reason
              );
            }
          });

          this.client.activate();
        },

        error: (erro) => {
          console.error('Erro ao obter token CSRF:', erro);
        }
      });
  }

  disconnect(): void {
    const client = this.client;
    this.client = undefined;

    if (client) {
      void client.deactivate();
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
    this.telemetryChangedSubject.complete();
  }

  private processarNotificacao(message: IMessage): void {
    console.log('Mensagem STOMP recebida:', message.body);

    const evento: TelemetryChangedEventDTO =
      JSON.parse(message.body);

    console.log('Nova telemetria:', evento);

    this.telemetryChangedSubject.next(evento);
  }
}
