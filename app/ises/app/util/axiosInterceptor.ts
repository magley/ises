import axios from "axios";
import { Packet } from "./packet";
import { getIpFromLocalStorage, getJWTStringOrNull } from "./localstorage";

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use((config) => {
    const packet: Packet = {
        srcIp: getIpFromLocalStorage(),
        destIp: "123.12.23.31",
        srcPort: "5173",
    }
    // According to the HTTP specification, GET does not support `body`. While
    // we _could_ ignore the spec, there's no guarantee that Spring Boot (or any
    // other middleware) won't just ignore it. That's why we use `params` here.
    config.params = packet;


    const jwt = getJWTStringOrNull();
    if (jwt != null) {
        config.headers.Authorization = "Bearer " + jwt;
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

axiosInstance.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    const data: object = error.response.data;
    if ('ipBlockedReason' in data) {
        window.location.replace("/banned");
    }
    return Promise.reject(error);
});

export default axiosInstance;