package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_menu")
@Data
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

}