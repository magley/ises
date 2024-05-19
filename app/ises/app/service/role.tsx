import { AxiosResponse } from "axios";
import axiosInstance from "~/util/axiosInterceptor";
import { ArticleDTO } from "./article";
import { PermissionDTO } from "./permission";

export interface RoleDTO {
    id: number,
    name: string,
    description: string,
    permissions: PermissionDTO[],
    parentId: number
}

export abstract class RoleService {
    static findAll(): Promise<AxiosResponse<RoleDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/auth/role`);
    }
}