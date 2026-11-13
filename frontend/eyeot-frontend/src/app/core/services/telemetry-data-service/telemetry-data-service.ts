import { Injectable, inject, signal } from '@angular/core';
import { PersistenceService } from '../persistence-service/persistence-service';
import { TelemetryPointDTO } from '../../model/dtos/telemetry-point.dto';
import { catchError, defer, finalize, map, Observable , of, Subject, Subscription, tap } from 'rxjs';
import { TelemetryChangedEventDTO, TelemetryWebsocketService } from '../telemetry-websocket-service/telemetry-websocket-service';
import { SensorStatistics } from '../../model/interfaces/sensor-statistics';

@Injectable()
export class TelemetryDataService {

  private readonly persistenceService = inject(PersistenceService);

  private readonly _pontos = signal<TelemetryPointDTO[]>([]);
  private readonly _ultimasLeituras = signal<TelemetryPointDTO[]>([]);

  private readonly telemetriasEmConsulta = new Set<number>();
  private readonly telemetriasProcessadas = new Set<number>();

  readonly pontos = this._pontos.asReadonly();
  readonly ultimasLeituras = this._ultimasLeituras.asReadonly();

  private readonly eventosPendentes = new Map<number, TelemetryChangedEventDTO>();

  private readonly websocketService = inject(TelemetryWebsocketService);

  private readonly telemetriasParaBuscarSubject = new Subject<number>();

  readonly telemetriasParaBuscar$ = this.telemetriasParaBuscarSubject.asObservable();

  private websocketSubscription?: Subscription;

  private carregandoHistorico = false;

  iniciarEscuta(): void {
    if (this.websocketSubscription) {
      return;
    }

    this.websocketSubscription =
      this.websocketService.telemetryChanged$.subscribe(evento => {
        this.receberNovaTelemetria(evento);
      });

    this.websocketService.connect();
  }


  private receberNovaTelemetria(
    evento: TelemetryChangedEventDTO
  ): void {
    if (!evento || !evento.idTelemetryData) {
      return;
    }

    if (this.carregandoHistorico) {
      this.adicionarEventoPendente(evento);
      return;
    }

    this.telemetriasParaBuscarSubject.next(evento.idTelemetryData);
  }

  definirCarregamentoHistorico(carregando: boolean): void {
    this.carregandoHistorico = carregando;

    if (!carregando) {
      const eventos = this.consumirEventosPendentes();

      eventos.forEach(evento => {
        this.telemetriasParaBuscarSubject.next(
          evento.idTelemetryData
        );
      });
    }
  }

  iniciarConsulta(idTelemetryData: number): boolean {
    if (
      this.telemetriasEmConsulta.has(idTelemetryData) ||
      this.telemetriasProcessadas.has(idTelemetryData)
    ) {
      return false;
    }

    this.telemetriasEmConsulta.add(idTelemetryData);
    return true;
  }

  finalizarConsulta(idTelemetryData: number): void {
    this.telemetriasEmConsulta.delete(idTelemetryData);
  }

  marcarComoProcessada(idTelemetryData: number): void {
    this.telemetriasProcessadas.add(idTelemetryData);
  }

  adicionarPontosSemDuplicar(
    novosPontos: TelemetryPointDTO[]
  ): void {
    this._pontos.update(pontosAtuais => {
      const idsExistentes = new Set(
        pontosAtuais.map(
          ponto => `${ponto.idTelemetryData}:${ponto.sensorIndex}`
        )
      );

      const pontosNovos = novosPontos.filter(ponto => {
        const chave = `${ponto.idTelemetryData}:${ponto.sensorIndex}`;

        if (idsExistentes.has(chave)) {
          return false;
        }

        idsExistentes.add(chave);
        return true;
      });

      return [...pontosAtuais, ...pontosNovos];
    });

    this.ordenarPontos(ponto =>
      ponto.timestamp ? new Date(ponto.timestamp).getTime() : 0
    );
  }


 definirPontos(pontos: TelemetryPointDTO[]): void {
    this._pontos.set(
      [...pontos].sort(
        (a, b) => this.obterTempo(a) - this.obterTempo(b)
      )
    );
  }

  private obterTempo(ponto: TelemetryPointDTO): number {
    return ponto.timestamp
      ? new Date(ponto.timestamp).getTime()
      : 0;
  }


  definirUltimasLeituras(pontos: TelemetryPointDTO[]): void {
    this._ultimasLeituras.set(pontos);
  }

  ordenarPontos(obterTempo: (ponto: TelemetryPointDTO) => number): void {
    this._pontos.update(pontos =>
      [...pontos].sort(
        (a, b) => obterTempo(a) - obterTempo(b)
      )
    );
  }

  atualizarUltimasLeituras(
    novasLeituras: TelemetryPointDTO[]
  ): void {
    this._ultimasLeituras.update(leituraAtual => {
      const atualizadas = [...leituraAtual];

      for (const novaLeitura of novasLeituras) {
        if (!novaLeitura.timestamp) {
          continue;
        }

        const indice = atualizadas.findIndex(
          leitura => leitura.sensorIndex === novaLeitura.sensorIndex
        );

        if (indice === -1) {
          atualizadas.push(novaLeitura);
          continue;
        }

        const leituraExistente = atualizadas[indice];

        const timestampNovo = new Date(
          novaLeitura.timestamp
        ).getTime();

        const timestampExistente = leituraExistente.timestamp
          ? new Date(leituraExistente.timestamp).getTime()
          : 0;

        if (timestampNovo > timestampExistente) {
          atualizadas[indice] = novaLeitura;
        }
      }

      return atualizadas;
    });
  }

  private extrairDados(response: any): TelemetryPointDTO[] {
    const dados = Array.isArray(response)
      ? response
      : response?.data;

    if (!Array.isArray(dados)) {
      throw new Error('Formato de resposta inválido.');
    }

    return dados as TelemetryPointDTO[];
  }

  buscarHistorico(
    idDevice: number,
    inicio: string,
    fim: string
  ): Observable<TelemetryPointDTO[]> {

    const url =
      `/v1/devices/${idDevice}/telemetry` +
      `?inicio=${encodeURIComponent(inicio)}` +
      `&fim=${encodeURIComponent(fim)}`;

    return this.persistenceService
      .getRequest(url)
      .pipe(
        map(response => this.extrairDados(response))
      );
  }

  buscarUltimasLeituras(
    idDevice: number
  ): Observable<TelemetryPointDTO[]> {

    const url = `/v1/devices/${idDevice}/telemetry/latest`;

    return this.persistenceService
      .getRequest(url)
      .pipe(
        map(response => this.extrairDados(response))
      );
  }

  buscarEstatisticas(
    idDevice: number,
    sensorIndex: number,
    summaryPeriod: number,
    start: string,
    end: string
  ): Observable<SensorStatistics[]> {

    const params = new URLSearchParams({
      summaryPeriod: String(summaryPeriod),
      start,
      end
    });

    const url =
      `/v1/devices/${idDevice}/sensors/${sensorIndex}/statistics?${params.toString()}`;

    return this.persistenceService
      .getRequest(url)
      .pipe(
        map(response => {
          if (response == null) {
            return [];
          }

          return response as SensorStatistics[];
        })
      );
  }

  buscarEstatisticasPaginadas(
  idDevice: number,
  sensorIndex: number,
  summaryPeriod: number,
  start: string,
  end: string,
  page: number,
  size: number
): Observable<any> {

  const params = new URLSearchParams({
    summaryPeriod: String(summaryPeriod),
    start,
    end,
    page: String(page),
    size: String(size)
  });

  const url =
    `/v1/devices/${idDevice}/sensors/${sensorIndex}/statistics/page?${params.toString()}`;

  return this.persistenceService.getRequest(url);
}

  buscarPorId(
    idTelemetryData: number
  ): Observable<TelemetryPointDTO[]> {

    return this.persistenceService
      .getRequest(`/v1/telemetry/${idTelemetryData}`)
      .pipe(
        map(response => this.extrairDados(response))
      );
  }

  adicionarEventoPendente(evento: TelemetryChangedEventDTO): void {
    this.eventosPendentes.set(evento.idTelemetryData, evento);
  }

  consumirEventosPendentes(): TelemetryChangedEventDTO[] {
    const eventos = Array.from(this.eventosPendentes.values());

    this.eventosPendentes.clear();

    return eventos;
  }

  desconectar(): void {
    this.websocketSubscription?.unsubscribe();
    this.websocketSubscription = undefined;
  }

  processarNovaTelemetria(
    idTelemetryData: number, idDevice: number, inicioPeriodo: string, fimPeriodo: string): Observable<void> {

    return defer(() => {
      if (!this.iniciarConsulta(idTelemetryData)) {
        return of(undefined);
      }

      return this.buscarPorId(idTelemetryData).pipe(
        tap(pontosRecebidos => {

          const pontosDoDispositivo = pontosRecebidos.filter(
            ponto => ponto.idDevice === idDevice
          );

          this.atualizarUltimasLeituras(pontosDoDispositivo);

          const inicio = new Date(inicioPeriodo).getTime();
          const fim = new Date(fimPeriodo).getTime();

          const pontosDoPeriodo = pontosDoDispositivo.filter(ponto => {
            if (!ponto.timestamp) {
              return false;
            }

            const timestamp = new Date(ponto.timestamp).getTime();

            return timestamp >= inicio && timestamp < fim;
          });

          this.marcarComoProcessada(idTelemetryData);

          if (pontosDoPeriodo.length === 0) {
            return;
          }

          this.adicionarPontosSemDuplicar(pontosDoPeriodo);
        }),
        map(() => undefined),
        catchError(erro => {
          console.error('Erro ao processar nova telemetria:', erro);
          return of(undefined);
        }),
        finalize(() => {
          this.finalizarConsulta(idTelemetryData);
        })
      );
    });
  }
}
