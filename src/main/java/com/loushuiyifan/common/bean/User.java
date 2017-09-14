package com.loushuiyifan.common.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "aweb_user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    private Long id;

    private String username;

    private String password;

    private String salt;

    private Byte locked;

    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Date lastLogin;


}