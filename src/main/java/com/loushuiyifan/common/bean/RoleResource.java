package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_role_resource")
@Data
public class RoleResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "resource_id")
    private Long resourceId;

}