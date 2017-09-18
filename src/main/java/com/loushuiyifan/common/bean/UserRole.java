package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.*;

@Table(name = "aweb_user_role")
@Data
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;


}