export interface DeviceFormDTO{
  id_domain: number;

  deviceId: string;
  enable: boolean;

  latitude: number | null;
  longitude: number | null;
  altitude: number | null;
}
