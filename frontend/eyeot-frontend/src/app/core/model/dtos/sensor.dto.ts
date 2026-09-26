import { SensorTypeDTO } from "./sensor-type.dto";

export interface SensorDTO {
  index: number;
  idDevice: number;
  sensorType: SensorTypeDTO;
}
