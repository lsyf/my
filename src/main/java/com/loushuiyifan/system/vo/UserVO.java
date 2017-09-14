package com.loushuiyifan.system.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-06-02 14:23.
 */
@Data
public class UserVO {
    Long id;
    String username;
    String password;
    Byte locked;
    List<AddVO> roles;
    List<AddVO> orgs;

    private String nickname;
    private String phone;
    private String email;
    private String avatar;


}
