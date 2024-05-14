/**
 * `Packet` is a record meant for simulating TCP/IP packets (and more) in the
 * applicative layer. Each client request must include an instance of `Packet`.
 */
export interface Packet {
    srcIp: string,
    destIp: string,
};