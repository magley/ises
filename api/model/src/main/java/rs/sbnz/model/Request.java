package rs.sbnz.model;

public class Request {
    private String srcIp;
    private String destIp;

    public Request() {
    }

    public Request(String srcIp, String destIp) {
        this.srcIp = srcIp;
        this.destIp = destIp;
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
}
