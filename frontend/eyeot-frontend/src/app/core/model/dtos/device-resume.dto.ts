export interface DeviceResumeDTO {
  id_device: number;
  deviceId: string;
  id_domain: number;
  domainName: string;
  creationDate: string;
  enable: boolean;
  latitude: number | null;
  longitude: number | null;
  altitude: number | null;
  numSensors: number;
}
