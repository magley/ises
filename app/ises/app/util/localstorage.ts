export function setJWT(jwt: string) {
    localStorage.setItem("jwt", jwt);
}

export function clearJWT() {
    localStorage.removeItem("jwt");
}

export function getJWTorNull(): string | null {
    return localStorage.getItem("jwt");
}