import { AxiosResponse } from "axios";
import axiosInstance from "~/util/axiosInterceptor";

export interface BlockEventDTO {
    ip: string,
    duration: number,
    reason: string,
}

export interface AlarmDTO {
    uuid: string,
    severity: string, // Should be enum
    description: string,
    handled: boolean,
    type: string, // Should be enum
}

export abstract class AdminService {
    static getBlocks(): Promise<AxiosResponse<BlockEventDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/admin/blocks`);
    }

    static unblockIP(ip: string): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/admin/unblock/${ip}`);
    }

    static getActiveAlarms(): Promise<AxiosResponse<AlarmDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/admin/alarms`);
    }

    static markAlarmAsRead(uuid: string): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/admin/alarms/${uuid}`);
    }

    static report(reportName: string): Promise<AxiosResponse<string[]>> {
        return axiosInstance.get(`http://localhost:8080/api/admin/report/${reportName}`);
    }
}