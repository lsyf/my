package com.loushuiyifan.mybatis.bean;

import javax.persistence.*;

@Table(name = "aweb_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String name;

    private String type;

    private String url;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "parent_ids")
    private String parentIds;

    private Short lvl;

    private Integer sorting;

    private Boolean available;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * @return parent_id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return parent_ids
     */
    public String getParentIds() {
        return parentIds;
    }

    /**
     * @param parentIds
     */
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds == null ? null : parentIds.trim();
    }

    /**
     * @return lvl
     */
    public Short getLvl() {
        return lvl;
    }

    /**
     * @param lvl
     */
    public void setLvl(Short lvl) {
        this.lvl = lvl;
    }

    /**
     * @return available
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * @param available
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getSorting() {
        return sorting;
    }

    public void setSorting(Integer sorting) {
        this.sorting = sorting;
    }
}