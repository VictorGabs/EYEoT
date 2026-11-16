import { Component, effect, ElementRef, inject, input, OnChanges, OnDestroy, signal, SimpleChanges, ViewChild} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import * as L from 'leaflet';

import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { SensorView } from '../../../../../core/model/interfaces/sensor-view';
import { SensorStatistics } from '../../../../../core/model/interfaces/sensor-statistics';
import { TelemetryDataService } from '../../../../../core/services/telemetry-data-service/telemetry-data-service';
import { finalize, Subscription } from 'rxjs';
import { MatTableModule } from '@angular/material/table';

Chart.register(...registerables);

interface EstatisticaNumerica {
  quantidade: number;
  media: number | null;
  mediana: number | null;
  moda: number | null;
  desvioPadrao: number | null;
}

interface EstatisticaBooleana {
  quantidade: number;
  verdadeiros: number;
  falsos: number;
  percentualVerdadeiro: number | null;
}

const iconesLeaflet = L.icon({
  iconUrl: 'assets/leaflet/marker-icon.png',
  shadowUrl: 'assets/leaflet/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

L.Marker.prototype.options.icon = iconesLeaflet;

@Component({
  selector: 'app-sensor-telemetry',
  standalone: true,
  imports: [MatIcon, MatCardModule, MatPaginatorModule, MatTableModule],
  templateUrl: './sensor-telemetry-component.html',
  styleUrl: './sensor-telemetry-component.scss'
})
export class SensorTelemetryComponent implements OnChanges, OnDestroy {

  constructor() {
    effect(() => {
      this.sensor();
      this.atualizarMapa();
    });

    effect(() => {
      const sensor = this.sensor();
      const inicio = this.inicioPeriodo();
      const fim = this.fimPeriodo();

      if (
        (sensor.dataType !== 1 && sensor.dataType !== 5) ||
        sensor.summaryPeriod == null ||
        !inicio ||
        !fim
      ) {
        this.estatisticas.set([]);
        this.carregandoEstatisticas.set(false);
        this.estatisticasSubscription?.unsubscribe();
        return;
      }


      this.buscarEstatisticas();
    });

    effect(() => {
      const sensor = this.sensor();
      const inicio = this.inicioPeriodo();
      const fim = this.fimPeriodo();

      const pagina = this.paginaAtual();
      const tamanho = this.tamanhoPagina();

      if (
        (sensor.dataType !== 1 && sensor.dataType !== 5) ||
        sensor.summaryPeriod == null ||
        !inicio ||
        !fim
      ) {
        this.estatisticasTabela.set([]);
        this.totalElements.set(0);
        this.carregandoTabela.set(false);
        this.estatisticasTabelaSubscription?.unsubscribe();
        return;
      }

      this.buscarEstatisticasPaginadas(pagina, tamanho);
    });

  }

  private readonly telemetryDataService = inject(TelemetryDataService);

  readonly estatisticas = signal<SensorStatistics[]>([]);
  readonly carregandoEstatisticas = signal(false);
  readonly estatisticasTabela = signal<SensorStatistics[]>([]);
  readonly carregandoTabela = signal(false);

  readonly colunasTabela = [
    'intervalo',
    'leituras',
    'minimo',
    'maximo',
    'amplitude',
    'soma',
    'media',
    'mediana',
    'desvioPadrao'
  ];

  readonly paginaAtual = signal(0);
  readonly tamanhoPagina = signal(10);

  readonly totalElements = signal(0);

  private estatisticasTabelaSubscription?: Subscription;

  private estatisticasSubscription?: Subscription;

  sensor = input.required<SensorView>();
  readonly inicioPeriodo = input.required<string>();
  readonly fimPeriodo = input.required<string>();

  private canvas?: HTMLCanvasElement;

  private statisticsCanvas?: HTMLCanvasElement;

  @ViewChild('statisticsCanvas')
  set statisticsCanvasRef(element: ElementRef<HTMLCanvasElement> | undefined) {
    this.statisticsCanvas = element?.nativeElement;
    this.atualizarGraficoMedias();
  }

  @ViewChild('sensorCanvas')
  set sensorCanvas(element: ElementRef<HTMLCanvasElement> | undefined) {
    this.canvas = element?.nativeElement;
    this.atualizarGrafico();
  }

  @ViewChild('gpsMap')
  set gpsMap(element: ElementRef<HTMLDivElement> | undefined) {
    this.elementoMapa = element?.nativeElement;
    this.atualizarMapa();
  }

  private mapa?: L.Map;
  private camadaMarcadores?: L.LayerGroup;
  private elementoMapa?: HTMLDivElement;

  private linhaRota?: L.Polyline;

  private grafico?: Chart<'line', (number | null)[], string>;
  private graficoMedias?: Chart<'line', (number | null)[], string>;

  private readonly cores = [
    '#004aad',
    '#e76f51',
    '#2a9d8f',
    '#9b5de5',
    '#f4a261',
    '#d62828'
  ];


  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes['sensor'] ||
      changes['inicioPeriodo'] ||
      changes['fimPeriodo']
    ) {
      this.paginaAtual.set(0);
    }

    if (changes['sensor']) {
      this.atualizarGrafico();
    }
  }
  ngOnDestroy(): void {
    this.estatisticasSubscription?.unsubscribe();
    this.grafico?.destroy();
    this.grafico = undefined;
    this.graficoMedias?.destroy();
    this.graficoMedias = undefined;
    this.mapa?.remove();
    this.mapa = undefined;
    this.camadaMarcadores = undefined;
  }

  private criarConfiguracao():
    ChartConfiguration<'line', (number | null)[], string> | null {

    const sensor = this.sensor();
    const pontos = sensor.pontos;

  const labels = pontos.map(
      ponto => this.formatarHorario(ponto.timestamp)
    );

    let datasets: ChartConfiguration<
      'line',
      (number | null)[],
      string
    >['data']['datasets'];

    if (sensor.dataType === 4) {
      datasets = [
        this.criarDataset('Eixo X', pontos.map(p => p.value), this.cores[0]),
        this.criarDataset('Eixo Y', pontos.map(p => p.value2), this.cores[1]),
        this.criarDataset('Eixo Z', pontos.map(p => p.value3), this.cores[2])
      ];
    } else if (sensor.dataType === 2) {
      datasets = [
        this.criarDataset(
          'Estado',
          pontos.map(p =>
            p.booleanValue == null ? null : Number(p.booleanValue)
          ),
          this.cores[0],
          true
        )
      ];
    } else {
      datasets = [
        this.criarDataset(
          sensor.physicalQuantity || sensor.sensorType,
          pontos.map(p => p.value),
          this.cores[0]
        )
      ];
    }

    return {
      type: 'line',
      data: { labels, datasets },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: false,
        interaction: {
          intersect: false,
          mode: 'index'
        },
        plugins: {
          legend: {
            display: datasets.length > 1 || sensor.dataType === 2,
            position: 'bottom'
          },
          tooltip: {
            callbacks: {
              label: contexto => {
                const valor = contexto.parsed.y;

                if (sensor.dataType === 2) {
                  return `Estado: ${valor === 1 ? 'Verdadeiro' : 'Falso'}`;
                }

                const unidade = sensor.measureUnit
                  ? ` ${sensor.measureUnit}`
                  : '';

                return `${contexto.dataset.label}: ${valor ?? 'Sem leitura'}${unidade}`;
              }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Data e hora'
            }
          },
          y: {
            title: {
              display: true,
              text: sensor.dataType === 2
                ? 'Estado'
                : sensor.measureUnit || sensor.physicalQuantity || 'Valor'
            },
            ...(sensor.dataType === 2
              ? {
                  min: 0,
                  max: 1,
                  ticks: {
                    stepSize: 1,
                    callback: (valor: string | number) =>
                      Number(valor) === 1 ? 'Verdadeiro' : 'Falso'
                  }
                }
              : {})
          }
        }
      }
    };
  }

  private criarDataset(
    label: string,
    valores: (number | null)[],
    cor: string,
    stepped = false
  ): {
    label: string;
    data: (number | null)[];
    borderColor: string;
    backgroundColor: string;
    borderWidth: number;
    pointRadius: number;
    tension: number;
    stepped: boolean;
    spanGaps: boolean;
    fill: boolean;
  } {
    return {
      label,
      data: valores,
      borderColor: cor,
      backgroundColor: cor,
      borderWidth: 2,
      pointRadius: valores.length > 30 ? 0 : 3,
      tension: stepped ? 0 : 0.25,
      stepped,
      spanGaps: false,
      fill: false
    };
  }

  private atualizarGrafico(): void {
    const canvas = this.canvas;

    if (!canvas || this.sensor().dataType === 3) {
      this.grafico?.destroy();
      this.grafico = undefined;
      return;
    }

    const configuracao = this.criarConfiguracao();

    if (!configuracao) {
      return;
    }

    if (!this.grafico) {
      this.grafico = new Chart(canvas, configuracao);
      return;
    }

    this.grafico.data.labels = configuracao.data.labels;
    this.grafico.data.datasets = configuracao.data.datasets;
    this.grafico.update('none');
  }

  private atualizarGraficoMedias(): void {
    const canvas = this.statisticsCanvas;

    if (!canvas || this.estatisticas().length === 0) {
      this.graficoMedias?.destroy();
      this.graficoMedias = undefined;
      return;
    }

    const estatisticas = this.estatisticas();

    const labels = estatisticas.map(stats =>
      this.formatarHorario(stats.intervalStart)
    );

    const valores = estatisticas.map(stats =>
      stats.averageValue
    );

    if (!this.graficoMedias) {
      this.graficoMedias = new Chart(canvas, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'Média',
            data: valores,
            borderColor: '#e76f51',
            backgroundColor: '#e76f51',
            borderWidth: 2,
            pointRadius: 4,
            pointHoverRadius: 7,
            tension: 0.25,
            fill: false
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: false,
          interaction: {
            intersect: true,
            mode: 'nearest'
          },
          plugins: {
            legend: {
              display: true,
              position: 'bottom'
            },
            tooltip: {
              callbacks: {
                title: items => {
                  const stats = estatisticas[items[0].dataIndex];

                  return `${this.formatarHorario(stats.intervalStart)} até ${this.formatarHorario(stats.intervalEnd)}`;
                },
                label: contexto => {
                  const stats = estatisticas[contexto.dataIndex];

                  const unidade = this.sensor().measureUnit
                    ? ` ${this.sensor().measureUnit}`
                    : '';

                  return [
                    `Média: ${this.formatarEstatistica(stats.averageValue)}${unidade}`,
                    `Mínimo: ${this.formatarEstatistica(stats.minValue)}${unidade}`,
                    `Máximo: ${this.formatarEstatistica(stats.maxValue)}${unidade}`,
                    `Mediana: ${this.formatarEstatistica(stats.medianValue)}${unidade}`,
                    `Amplitude: ${this.formatarEstatistica(stats.amplitudeValue)}${unidade}`,
                    `Soma: ${this.formatarEstatistica(stats.sumValue)}${unidade}`,
                    `Desvio padrão populacional: ${this.formatarEstatistica(stats.standardDeviation)}${unidade}`,
                    `Leituras: ${stats.totalCount}`,
                    `Resumo gerado em: ${this.formatarHorario(stats.aggregatedAt)}`
                  ];
                }
              }
            }
          },
          scales: {
            x: {
              title: {
                display: true,
                text: 'Intervalo'
              }
            },
            y: {
              title: {
                display: true,
                text: this.sensor().measureUnit ||
                  this.sensor().physicalQuantity ||
                  'Média'
              }
            }
          }
        }
      });

      return;
    }

    this.graficoMedias.data.labels = labels;
    this.graficoMedias.data.datasets[0].data = valores;
    this.graficoMedias.update('none');
  }

  private atualizarMapa(): void {
    if (this.sensor().dataType !== 3 || !this.elementoMapa) {
      return;
    }

    this.inicializarMapa();

    if (!this.mapa || !this.camadaMarcadores) {
      return;
    }

    this.camadaMarcadores.clearLayers();
    this.linhaRota?.remove();

    const coordenadas: L.LatLngExpression[] = [];

    for (const ponto of this.sensor().pontos) {
      const latitude = Number(ponto.value);
      const longitude = Number(ponto.value2);

      if (
        !Number.isFinite(latitude) ||
        !Number.isFinite(longitude) ||
        latitude < -90 ||
        latitude > 90 ||
        longitude < -180 ||
        longitude > 180
      ) {
        continue;
      }

      const posicao: L.LatLngExpression = [latitude, longitude];

      coordenadas.push(posicao);

      const conteudo = document.createElement('div');

      const data = document.createElement('strong');
      data.textContent = ponto.timestamp
        ? new Date(ponto.timestamp).toLocaleString('pt-BR')
        : 'Data não disponível';

      const latitudeTexto = document.createElement('p');
      latitudeTexto.textContent = `Latitude: ${latitude}`;

      const longitudeTexto = document.createElement('p');
      longitudeTexto.textContent = `Longitude: ${longitude}`;

      const altitudeTexto = document.createElement('p');
      altitudeTexto.textContent =
        ponto.value3 != null
          ? `Altitude: ${ponto.value3} m`
          : 'Altitude não disponível';

      conteudo.append(
        data,
        latitudeTexto,
        longitudeTexto,
        altitudeTexto
      );

      L.marker(posicao)
        .bindPopup(conteudo)
        .addTo(this.camadaMarcadores);
    }

    if (coordenadas.length > 1) {
      this.linhaRota = L.polyline(coordenadas, {
        color: '#004aad',
        weight: 4,
        opacity: 0.8,
        smoothFactor: 1
      }).addTo(this.mapa);
    }

    requestAnimationFrame(() => {
      this.mapa?.invalidateSize();

      if (coordenadas.length > 0) {
        const ultimoPonto = coordenadas[coordenadas.length - 1];

        this.mapa?.setView(ultimoPonto, 15);
      }
    });
  }

  private inicializarMapa(): void {
    const elemento = this.elementoMapa;

    if (!elemento || this.mapa) {
      return;
    }

    this.mapa = L.map(elemento).setView([-15.78, -47.93], 5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19
    }).addTo(this.mapa);

    this.camadaMarcadores = L.layerGroup().addTo(this.mapa);

    requestAnimationFrame(() => {
      this.mapa?.invalidateSize();
    });
  }

  private buscarEstatisticasPaginadas(
    pagina: number,
    tamanho: number
  ): void {
    const sensor = this.sensor();

    this.estatisticasTabelaSubscription?.unsubscribe();

    this.carregandoTabela.set(true);

    this.estatisticasTabelaSubscription =
      this.telemetryDataService
        .buscarEstatisticasPaginadas(
          sensor.idDevice,
          sensor.sensorIndex,
          sensor.summaryPeriod!,
          this.inicioPeriodo(),
          this.fimPeriodo(),
          pagina,
          tamanho
        )
        .pipe(
          finalize(() => {
            this.carregandoTabela.set(false);
          })
        )
        .subscribe({
          next: response => {
            this.estatisticasTabela.set(response?.data ?? []);
            this.totalElements.set(response?.totalElements ?? 0);
          },
          error: erro => {
            console.error(
              'Erro ao buscar estatísticas paginadas:',
              erro
            );

            this.estatisticasTabela.set([]);
            this.totalElements.set(0);
          }
        });
  }

  alterarPagina(event: PageEvent): void {
    this.paginaAtual.set(event.pageIndex);
    this.tamanhoPagina.set(event.pageSize);
  }

  formatarHorario(timestamp: string | null): string {
    if (!timestamp) return 'Data desconhecida';

    const data = new Date(timestamp);

    return Number.isNaN(data.getTime())
      ? 'Data inválida'
      : data.toLocaleString('pt-BR');
  }

  formatarCoordenada(valor: number | null, unidade: string): string {
    return valor == null ? '—' : `${valor} ${unidade}`;
  }

  formatarBooleano(valor: boolean | null): string {
    if (valor == null) return 'Sem leitura';
    return valor ? 'Verdadeiro' : 'Falso';
  }


  calcularEstatisticasNumericas(
    valoresRecebidos: (number | null | undefined)[]
  ): EstatisticaNumerica {
    const valores = valoresRecebidos
      .filter((valor): valor is number =>
        typeof valor === 'number' && Number.isFinite(valor)
      )
      .sort((a, b) => a - b);

    if (valores.length === 0) {
      return {
        quantidade: 0,
        media: null,
        mediana: null,
        moda: null,
        desvioPadrao: null
      };
    }

    const quantidade = valores.length;
    const media = valores.reduce((soma, valor) => soma + valor, 0) / quantidade;

    const mediana = quantidade % 2 === 1
      ? valores[Math.floor(quantidade / 2)]
      : (
          valores[quantidade / 2 - 1] +
          valores[quantidade / 2]
        ) / 2;

    const frequencias = new Map<number, number>();

    for (const valor of valores) {
      frequencias.set(valor, (frequencias.get(valor) ?? 0) + 1);
    }

    const maiorFrequencia = Math.max(...frequencias.values());
    const modas = [...frequencias.entries()]
      .filter(([, frequencia]) => frequencia === maiorFrequencia)
      .map(([valor]) => valor);

    const moda = maiorFrequencia > 1 ? modas[0] : null;

    const desvioPadrao = quantidade > 1
      ? Math.sqrt(
          valores.reduce(
            (total, valor) => total + Math.pow(valor - media, 2),
            0
          ) / (quantidade - 1)
        )
      : null;

    return { quantidade, media, mediana, moda, desvioPadrao };
  }

  calcularEstatisticasBooleanas(
    valoresRecebidos: (boolean | null | undefined)[]
  ): EstatisticaBooleana {
    const valores = valoresRecebidos.filter(
      (valor): valor is boolean => typeof valor === 'boolean'
    );

    const verdadeiros = valores.filter(Boolean).length;
    const falsos = valores.length - verdadeiros;

    return {
      quantidade: valores.length,
      verdadeiros,
      falsos,
      percentualVerdadeiro: valores.length > 0
        ? (verdadeiros / valores.length) * 100
        : null
    };
  }

  formatarEstatistica(valor: number | null): string {
    return valor == null
      ? '—'
      : valor.toLocaleString('pt-BR', { maximumFractionDigits: 4 });
  }

  private buscarEstatisticas(): void {
    const sensor = this.sensor();

    this.estatisticasSubscription?.unsubscribe();

    this.estatisticas.set([]);
    this.carregandoEstatisticas.set(true);

    this.estatisticasSubscription =
      this.telemetryDataService
        .buscarEstatisticas(
          sensor.idDevice,
          sensor.sensorIndex,
          sensor.summaryPeriod!,
          this.inicioPeriodo(),
          this.fimPeriodo()
        )
        .pipe(
          finalize(() => {
            this.carregandoEstatisticas.set(false);
          })
        )
        .subscribe({
          next: resultado => {
            this.estatisticas.set(resultado ?? []);
            this.atualizarGraficoMedias();
          },
          error: erro => {
            console.error(
              'Erro ao buscar estatísticas do sensor:',
              erro
            );

            this.estatisticas.set([]);
            this.atualizarGraficoMedias();
          }
        });
  }
}

