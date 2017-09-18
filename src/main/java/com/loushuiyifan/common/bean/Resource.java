package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_resource")
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String name;

    private String type;

    @Column(name = "resource_id")
    private Long resourceId;

    private Boolean alls;


}