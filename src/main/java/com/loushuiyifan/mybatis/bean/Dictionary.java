package com.loushuiyifan.mybatis.bean;

import javax.persistence.*;

@Table(name = "aweb_dictionary")
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String name;
    private String data;
    private Integer sorting;
    private String remark;

    @Column(name = "parent_id")
    private Long parentId;


    private Boolean available;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getSorting() {
        return sorting;
    }

    public void setSorting(Integer sorting) {
        this.sorting = sorting;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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


}