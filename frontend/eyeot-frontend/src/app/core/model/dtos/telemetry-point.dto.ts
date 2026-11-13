import { SensorTypeDataTypeEnum } from "../../enuns/sensor-type-data-type.enum";


export interface TelemetryPointDTO {
  idTelemetryData: number;
  idDevice: number;
  sensorIndex: number;
  sensorType: string;
  dataType: SensorTypeDataTypeEnum;
  physicalQuantity: string;
  measureUnit: string;
  timestamp: string | null;
  value: number | null;
  value2: number | null;
  value3: number | null;
  booleanValue: boolean | null;
}
