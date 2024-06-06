package rs.sbnz.model;

public class AlarmRemove {
    /**
     * UUID of the alarm to remove.
     */
    private String uuid;

    public AlarmRemove() {
    }

    public AlarmRemove(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
