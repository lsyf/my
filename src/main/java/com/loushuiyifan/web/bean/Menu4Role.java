package com.loushuiyifan.web.bean;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 11:04.
 */
public class Menu4Role {

    private Long id;

    private String name;

    private String type;

    private String url;

    private Long parent_id;

    private String parent_ids;

    private Short lvl;

    private Boolean available;

    long resource_id;
    boolean alls;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Short getLvl() {
        return lvl;
    }

    public void setLvl(Short lvl) {
        this.lvl = lvl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
