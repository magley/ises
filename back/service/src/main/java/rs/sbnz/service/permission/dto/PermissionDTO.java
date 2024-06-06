package rs.sbnz.service.permission.dto;

import rs.sbnz.model.Permission;

public class PermissionDTO {
    private Long id;
    private String code;
    private String name;
    private String description;

    public PermissionDTO() {
    }

    public PermissionDTO(Long id, String code, String name, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public PermissionDTO(Permission p) {
        this.id = p.getId();
        this.code = p.getCode();
        this.name = p.getName();
        this.description = p.getDescription();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
