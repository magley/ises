package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

import rs.sbnz.model.NoteType;
import rs.sbnz.model.User;

@Role(Role.Type.EVENT)
@Expires("6h")
public class Note {
    private Long id;
    
    /** IP address of the user receiving a note. If `user` is specified, this
     * should be empty. */
    private String ip;

    /** User receiving a note. If `ip` is specified, this should be null. */
    private User user;

    /** Number of points this note is worth. There's no intrinsic semantics to
     * this value: it's up to you to decide. */
    private Long points;

    /** Type of note. */
    private NoteType type;

    /** Flag to check whether this note was used in a block. */
    private boolean usedInBlock = false;

    public Note() {
    }

    public Note(Long id, String ip, Long points, NoteType type) {
        this.id = id;
        this.ip = ip;
        this.user = null;
        this.points = points;
        this.type = type;
        this.usedInBlock = false;
    }

    public Note(Long id, User user, Long points, NoteType type) {
        this.id = id;
        this.ip = null;
        this.user = user;
        this.points = points;
        this.type = type;
        this.usedInBlock = false;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPoints() {
        return this.points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public NoteType getType() {
        return this.type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public boolean isUsedInBlock() {
        return this.usedInBlock;
    }

    public void setUsedInBlock(boolean usedInBlock) {
        this.usedInBlock = usedInBlock;
    }
}
