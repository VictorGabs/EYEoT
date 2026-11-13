import { TelemetryPointDTO } from "../dtos/telemetry-point.dto";

export interface SensorView {
  idDevice: number;
  sensorIndex: number;
  sensorType: string;
  dataType: number;
  physicalQuantity: string;
  measureUnit: string;
  pontos: TelemetryPointDTO[];
  ultimaLeitura: TelemetryPointDTO | null;
  summaryPeriod: number | null;
}
