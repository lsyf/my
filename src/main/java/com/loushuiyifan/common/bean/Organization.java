package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_organization")
@Data
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String name;
    private String data;
    private Integer sorting;
    private Integer lvl;
    private String remark;
    private Long parentId;
    private String parentIds;
    private Byte available;
    private String type;//组织类型

}