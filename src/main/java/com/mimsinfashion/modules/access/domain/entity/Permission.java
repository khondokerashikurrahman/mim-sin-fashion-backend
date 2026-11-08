package com.mimsinfashion.modules.access.domain.entity;

import com.mimsinfashion.common.domain.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_permission_code", columnNames = "code")
})
public class Permission extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    protected Permission() {
    }

    public static Permission create(String code, String name) {
        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        return permission;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
