export interface DeviceFormDTO{
  idDomain: number;

  deviceId: string;
  enable: boolean;

  latitude: number | null;
  longitude: number | null;
  altitude: number | null;
}
