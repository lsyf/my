package com.loushuiyifan.web.bean;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-22 14:51.
 */
public class RoleVO {
    long id;
    String name;
    String role;
    List<AddVO> menu;
    List<AddVO> permission;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<AddVO> getMenu() {
        return menu;
    }

    public void setMenu(List<AddVO> menu) {
        this.menu = menu;
    }

    public List<AddVO> getPermission() {
        return permission;
    }

    public void setPermission(List<AddVO> permission) {
        this.permission = permission;
    }
}


