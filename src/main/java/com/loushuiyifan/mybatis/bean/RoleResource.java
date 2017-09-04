package com.loushuiyifan.mybatis.bean;

import javax.persistence.*;

@Table(name = "aweb_role_resource")
public class RoleResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "resource_id")
    private Long resourceId;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return role_id
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return resource_id
     */
    public Long getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId
     */
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}