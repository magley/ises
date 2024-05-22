package rs.sbnz.service.role.dto;

import java.util.List;

import rs.sbnz.model.Role;
import rs.sbnz.service.permission.dto.PermissionDTO;

public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private List<PermissionDTO> permissions;
    private Long parentId;

    public RoleDTO() {
    }

    public RoleDTO(Long id, String name, String description, List<PermissionDTO> permissions, Long parentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.parentId = parentId;
    }

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.permissions = role.getPermissions().stream().map(p -> new PermissionDTO(p)).toList();
        if (role.getParent() != null) {
            this.parentId = role.getParent().getId();
        } else {
            this.parentId = -1L;
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PermissionDTO> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
