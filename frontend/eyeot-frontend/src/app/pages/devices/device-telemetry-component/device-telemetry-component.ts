import { ChangeDetectorRef, Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {TelemetryWebsocketService } from '../../../core/services/telemetry-websocket-service/telemetry-websocket-service';
import { catchError, finalize, of, Subscription, tap } from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { SensorService } from '../../../core/services/sensor-service/sensor-service';
import { SensorResumeDTO } from '../../../core/model/dtos/sensor-resume.dto';
import { SensorTelemetryComponent } from './components/sensor-telemetry-component/sensor-telemetry-component';
import { TelemetryDataService } from '../../../core/services/telemetry-data-service/telemetry-data-service';
import { SensorView } from '../../../core/model/interfaces/sensor-view';


type PeriodoTelemetria =
  | 'dia'
  | 'semana'
  | 'mes'
  | 'ano'
  | 'personalizado';


@Component({
  imports: [RouterLink,
    MatIcon,
    MatButton,
    MatCardModule,
    MatProgressSpinnerModule, SensorTelemetryComponent],
  providers: [TelemetryDataService],
  selector: 'app-device-telemetry-component',
  styleUrl: './device-telemetry-component.scss',
  templateUrl: './device-telemetry-component.html',
})
export class DeviceTelemetryComponent implements OnInit, OnDestroy {

  private readonly route = inject(ActivatedRoute);
  protected readonly telemetryDataService = inject(TelemetryDataService);
  private readonly websocketService = inject(TelemetryWebsocketService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly sensorService = inject(SensorService);

  sensoresCadastrados = signal<SensorResumeDTO[]>([]);

  periodoSelecionado: PeriodoTelemetria = 'dia';

  inicioPersonalizado = '';
  fimPersonalizado = '';

  inicioPeriodo = '';
  fimPeriodo = '';

  private readonly subscriptions = new Subscription();


  idDevice!: number;

  carregando = true;
  erro: string | null = null;

  readonly sensores = computed<SensorView[]>(() => {
    const pontos = this.telemetryDataService.pontos();
    const ultimasLeituras = this.telemetryDataService.ultimasLeituras();

    return this.sensoresCadastrados().map(sensor => {
      const pontosSensor = pontos
        .filter(ponto => ponto.sensorIndex === sensor.index)
        .sort((a, b) => {
          const timestampA = a.timestamp
            ? new Date(a.timestamp).getTime()
            : 0;

          const timestampB = b.timestamp
            ? new Date(b.timestamp).getTime()
            : 0;

          return timestampA - timestampB;
        });

      const ultimaLeitura = ultimasLeituras.find(
        leitura => leitura.sensorIndex === sensor.index
      ) ?? null;

      return {
        idDevice: sensor.id_device,
        sensorIndex: sensor.index,
        sensorType: sensor.sensorTypeDescription,
        dataType: sensor.dataType,
        physicalQuantity: sensor.physicalQuantity,
        measureUnit: sensor.measureUnit,
        pontos: pontosSensor,
        ultimaLeitura,
        summaryPeriod: sensor.summaryPeriod
      };
    });
  });

  ngOnInit(): void {

    const id = Number(this.route.snapshot.paramMap.get('idDevice'));

    if (!Number.isInteger(id) || id <= 0) {
      this.carregando = false;
      this.erro = 'Identificador de dispositivo inválido.';
      return;
    }

    this.idDevice = id;

    this.telemetryDataService.iniciarEscuta();

    this.subscriptions.add(
      this.telemetryDataService.telemetriasParaBuscar$.subscribe(
        idTelemetryData => {
          this.buscarTelemetria(idTelemetryData);
        }
      )
    );

    this.carregarSensores();
    this.carregarHistorico();
    this.carregarUltimasLeituras();

  }

  private carregarHistorico(): void {
    if (!this.inicioPeriodo || !this.fimPeriodo) {
      const intervalo = this.calcularIntervalo('dia');

      this.inicioPeriodo = this.formatarDataApi(intervalo.inicio);
      this.fimPeriodo = this.formatarDataApi(intervalo.fim);
    }

    this.telemetryDataService.definirCarregamentoHistorico(true);
    this.carregando = true;
    this.erro = null;

    this.subscriptions.add(
      this.telemetryDataService
        .buscarHistorico(
          this.idDevice,
          this.inicioPeriodo,
          this.fimPeriodo
        )
        .pipe(
          tap(dados => {
            this.telemetryDataService.definirPontos(dados);
          }),
          catchError((error: unknown) => {
            console.error('Erro ao carregar histórico:', error);
            this.telemetryDataService.definirPontos([]);
            this.erro = 'Não foi possível carregar as leituras do período.';
            return of(null);
          }),
          finalize(() => {
            this.carregando = false;
            this.telemetryDataService.definirCarregamentoHistorico(false);
            this.cdr.detectChanges();

          })
        )
        .subscribe()
    );
  }

  private carregarUltimasLeituras(): void {
    this.subscriptions.add(
      this.telemetryDataService
        .buscarUltimasLeituras(this.idDevice)
        .pipe(
          tap(dados => {
           this.telemetryDataService.definirUltimasLeituras(dados);
          }),
          catchError((error: unknown) => {
            console.error('Erro ao carregar últimas leituras:', error);
            this.telemetryDataService.definirUltimasLeituras([]);
            return of(null);
          })
        )
        .subscribe()
    );
  }

  private buscarTelemetria(idTelemetryData: number): void {
    this.subscriptions.add(
      this.telemetryDataService
        .processarNovaTelemetria(
          idTelemetryData,
          this.idDevice,
          this.inicioPeriodo,
          this.fimPeriodo
        )
        .subscribe()
    );
  }

  formatarHorario(timestamp: string | null): string {
    if (!timestamp) {
      return 'Data desconhecida';
    }

    const data = new Date(timestamp);

    return Number.isNaN(data.getTime())
      ? 'Data inválida'
      : data.toLocaleString('pt-BR');
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.telemetryDataService.desconectar();
    this.websocketService.disconnect();
  }

  tentarNovamente(): void {
    this.carregarHistorico();
  }

  private formatarDataApi(data: Date): string {
    const pad = (valor: number) => String(valor).padStart(2, '0');

    return [
      `${data.getFullYear()}-${pad(data.getMonth() + 1)}-${pad(data.getDate())}`,
      `${pad(data.getHours())}:${pad(data.getMinutes())}:${pad(data.getSeconds())}`
    ].join('T');
  }

  private inicioDoDia(data: Date): Date {
    const resultado = new Date(data);
    resultado.setHours(0, 0, 0, 0);
    return resultado;
  }

  private calcularIntervalo(periodo: Exclude<PeriodoTelemetria, 'personalizado'>): {
    inicio: Date;
    fim: Date;
  } {
    const agora = new Date();
    const inicio = this.inicioDoDia(agora);
    const fim = new Date(inicio);

    switch (periodo) {
      case 'dia':
        fim.setDate(fim.getDate() + 1);
        break;

      case 'semana': {
        const diaSemana = inicio.getDay();
        const diasDesdeSegunda = (diaSemana + 6) % 7;

        inicio.setDate(inicio.getDate() - diasDesdeSegunda);
        fim.setTime(inicio.getTime());
        fim.setDate(fim.getDate() + 7);
        break;
      }

      case 'mes':
        inicio.setDate(1);
        fim.setTime(inicio.getTime());
        fim.setMonth(fim.getMonth() + 1);
        break;

      case 'ano':
        inicio.setMonth(0, 1);
        fim.setTime(inicio.getTime());
        fim.setFullYear(fim.getFullYear() + 1);
        break;
    }

    return { inicio, fim };
  }

  selecionarPeriodo(periodo: PeriodoTelemetria): void {
    this.periodoSelecionado = periodo;

    if (periodo === 'personalizado') {
      return;
    }

    const intervalo = this.calcularIntervalo(periodo);

    this.inicioPeriodo = this.formatarDataApi(intervalo.inicio);
    this.fimPeriodo = this.formatarDataApi(intervalo.fim);

    this.carregarHistorico();
  }

  aplicarPeriodoPersonalizado(): void {
    if (!this.inicioPersonalizado || !this.fimPersonalizado) {
      this.erro = 'Informe a data e hora inicial e final.';
      return;
    }

    const inicio = new Date(this.inicioPersonalizado);
    const fim = new Date(this.fimPersonalizado);

    if (
      Number.isNaN(inicio.getTime()) ||
      Number.isNaN(fim.getTime()) ||
      inicio >= fim
    ) {
      this.erro = 'A data inicial deve ser anterior à data final.';
      return;
    }

    this.inicioPeriodo = this.formatarDataApi(inicio);
    this.fimPeriodo = this.formatarDataApi(fim);

    this.carregarHistorico();
  }

  private carregarSensores(): void {
    this.subscriptions.add(
    this.sensorService.listarSensoresDoDispositivo(this.idDevice)
      .pipe(
        tap((response: any) => {
          const dados = Array.isArray(response)
            ? response
            : response?.data;

          if (!Array.isArray(dados)) {
            throw new Error('Formato de resposta dos sensores inválido.');
          }

          this.sensoresCadastrados.set(dados);
          this.cdr.detectChanges();
        }),
        catchError((error: unknown) => {
          console.error('Erro ao carregar sensores:', error);
          this.erro = 'Não foi possível carregar os sensores do dispositivo.';
          return of(null);
        })
      )
      .subscribe()
    );
  }
}
