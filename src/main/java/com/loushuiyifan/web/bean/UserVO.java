package com.loushuiyifan.web.bean;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 14:23.
 */
public class UserVO {
    long id;
    String username;
    String password;
    boolean locked;
    List<AddVO> roles;
    List<AddVO> orgs;

    public List<AddVO> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<AddVO> orgs) {
        this.orgs = orgs;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<AddVO> getRoles() {
        return roles;
    }

    public void setRoles(List<AddVO> roles) {
        this.roles = roles;
    }
}
