export interface SensorStatistics {
  idDevice: number;
  sensorIndex: number;
  intervalStart: string;
  intervalEnd: string;
  summaryPeriod: number;
  minValue: number;
  maxValue: number;
  amplitudeValue: number;
  sumValue: number;
  sumValueSquared: number;
  averageValue: number;
  medianValue: number;
  standardDeviation: number;
  totalCount: number;
  aggregatedAt: string;
}
