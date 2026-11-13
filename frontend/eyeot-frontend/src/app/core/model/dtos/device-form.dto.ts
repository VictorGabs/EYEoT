export interface DeviceFormDTO{
  id_domain: number;

  enable: boolean;

  latitude: number | null;
  longitude: number | null;
  altitude: number | null;

  id_owner?: number;
}
