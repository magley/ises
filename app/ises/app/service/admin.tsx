import { AxiosResponse } from "axios";
import axiosInstance from "~/util/axiosInterceptor";

export interface BlockEventDTO {
    ip: string,
    duration: number,
    reason: string,
}


export abstract class AdminService {
    static getBlocks(): Promise<AxiosResponse<BlockEventDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/admin/blocks`);
    }

    static unblockIP(ip: string): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/admin/unblock/${ip}`);
    }
}