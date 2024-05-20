package rs.sbnz.service.user.dto;

public class SetUserRoleDTO {
    private Long userId;
    private Long roleId;

    public SetUserRoleDTO() {
    }

    public SetUserRoleDTO(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
