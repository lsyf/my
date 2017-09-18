package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_user_organization")
@Data
public class UserOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "org_id")
    private Long orgId;


}