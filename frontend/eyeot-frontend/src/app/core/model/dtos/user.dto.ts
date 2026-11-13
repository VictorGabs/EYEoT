import { RolesEnum } from "../../enuns/roles-enum";

export interface UserDTO {
    id_user: number,
    role: RolesEnum,
    name: string,
    phone?: string,
    email1: string,
    email2?: string,
}
