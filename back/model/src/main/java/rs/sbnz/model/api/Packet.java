package rs.sbnz.model.api;

/**
 * Packet is an encapsulation for all the simulated fields sent with every HTTP
 * request. Each endpoint should have a `Packet packet` parameter (no
 * `@RequestBody`, because this is a request param).)
 */
public class Packet {
    private String srcIp;
    private String destIp;
    private String srcPort;

    public Packet() {
    }

    public Packet(String srcIp, String destIp, String srcPort) {
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.srcPort = srcPort;
    }

    public String getSrcIp() {
        return this.srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDestIp() {
        return this.destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getSrcPort() {
        return this.srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }
}
