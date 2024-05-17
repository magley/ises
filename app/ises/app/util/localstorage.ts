import { Buffer } from 'buffer';

export interface JWTStruct {
    sub: string,
    role: string,
    id: number,
    nagPassword: boolean
};

export function setJWT(jwt: string) {
    localStorage.setItem("jwt", jwt);
}

export function clearJWT() {
    localStorage.removeItem("jwt");
}

export function getJWTStringOrNull(): string | null {
    return localStorage.getItem("jwt");
}

export function getJWT(jwt: string): JWTStruct {
    let parts = jwt.split(".");
    for (let i = 0; i < parts.length; i++) {
        parts[i] = Buffer.from(parts[i], 'base64').toString();
    }
    let token: JWTStruct = JSON.parse(parts[1]);
    return token;
}

export function getJwtRole(): string {
    const jwtString = getJWTStringOrNull();
    if (jwtString == null) {
        return "";
    }
    return getJWT(jwtString).role;
}

export function getJwtEmail(): string {
    const jwtString = getJWTStringOrNull();
    if (jwtString == null) {
        return "";
    }
    return getJWT(jwtString).sub;
}
