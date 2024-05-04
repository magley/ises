package rs.sbnz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String srcIp;

    @Column
    private String destIp;

    public Request(Long id, String srcIp, String destIp) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
    }

    public Request() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
