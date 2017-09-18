package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_dictionary")
@Data
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


}