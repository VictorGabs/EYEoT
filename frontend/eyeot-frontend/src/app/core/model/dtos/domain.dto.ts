import { DeviceDTO } from "./device.dto";
import { UserDTO } from "./user.dto";

export interface DomainDTO{
  id_domain:number,
  user:UserDTO,
  domainName:string,
  description:string,
  devices?:DeviceDTO[]
}
