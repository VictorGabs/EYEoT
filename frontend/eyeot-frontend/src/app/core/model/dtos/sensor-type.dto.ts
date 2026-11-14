
export interface SensorTypeDTO {
  id?: number;
  description: string;
  dataType: number;

  minValue: number | null;
  maxValue: number | null;

  physicalQuantity: string;
  measureUnit: string;

  averageInterval: number | null;

  retentionPeriod: number | null;
  summaryPeriod: number | null;
}
