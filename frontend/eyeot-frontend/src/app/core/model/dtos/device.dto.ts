import { SensorDTO } from "./sensor.dto";

export interface DeviceDTO{
  idDevice: number;
  idDomain: number;

  deviceId: string;
  creationDate: string;
  enable: boolean;

  latitude: number | null;
  longitude: number | null;
  altitude: number | null;

  sensors: SensorDTO[];
}
