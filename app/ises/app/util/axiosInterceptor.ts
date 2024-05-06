import axios from "axios";
import { Packet } from "./packet";
import { getJWTorNull } from "./localstorage";

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use((config) => {
    const packet: Packet = {
        srcIp: "123.12.23.31",
        destIp: "123.12.23.31"
    }
    // According to the HTTP specification, GET does not support `body`. While
    // we _could_ ignore the spec, there's no guarantee that Spring Boot (or any
    // other middleware) won't just ignore it. That's why we use `params` here.
    config.params = packet;


    const jwt = getJWTorNull();
    if (jwt != null) {
        config.headers.Authorization = "Bearer " + jwt;
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

export default axiosInstance;