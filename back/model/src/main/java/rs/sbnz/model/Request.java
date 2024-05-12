package rs.sbnz.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Entity
@Expires("6h")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String srcIp;

    @Column
    private String destIp;

    @Column
    private String srcPort;

    @Column
    private Date timestamp;

    @ManyToOne
    private User user;

    public Request(Long id, String srcIp, String destIp, String srcPort) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.srcPort = srcPort;
        this.timestamp = new Date();
        this.user = null;
    }

    public Request(Long id, String srcIp, String destIp, String srcPort, Date timestamp) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.srcPort = srcPort;
        this.timestamp = timestamp;
        this.user = null;
    }

    public Request(Long id, String srcIp, String destIp, String srcPort, User user) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.timestamp = new Date();
        this.user = user;
    }

    public Request(Long id, String srcIp, String destIp, String srcPort, User user, Date timestamp) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.timestamp = timestamp;
        this.user = user;
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

    public String getSrcPort() {
        return this.srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
