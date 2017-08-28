package com.loushuiyifan.web.bean;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 11:06.
 */
public class Permission4Role {
    long resource_id;
    boolean alls;

    private Long id;

    private String name;

    private String url;

    private Long parent_id;

    private String parent_ids;

    private String permission;

    private Boolean available;


    public long getResource_id() {
        return resource_id;
    }

    public void setResource_id(long resource_id) {
        this.resource_id = resource_id;
    }

    public boolean isAlls() {
        return alls;
    }

    public void setAlls(boolean alls) {
        this.alls = alls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_ids() {
        return parent_ids;
    }

    public void setParent_ids(String parent_ids) {
        this.parent_ids = parent_ids;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
