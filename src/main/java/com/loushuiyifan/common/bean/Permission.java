package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "aweb_permission")
@Data
public class Permission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String name;

    private String url;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "parent_ids")
    private String parentIds;

    private String permission;

    private Boolean available;

   }