import { AxiosResponse } from "axios";
import axiosInstance from "~/util/axiosInterceptor";

export interface UserDTO {
    id: number,
    email: string,
    name: string,
    lastName: string,
    role: string // hrbac role name
}

export abstract class UserService {
    static hasPermission(permissionCode: string): Promise<AxiosResponse<boolean>> {
        return axiosInstance.get(`http://localhost:8080/api/auth/permission?permissionCode=${permissionCode}`);
    }

    static findById(id: number): Promise<AxiosResponse<UserDTO>> {
        return axiosInstance.get(`http://localhost:8080/api/user/${id}`);
    }
}