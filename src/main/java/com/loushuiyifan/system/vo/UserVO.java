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

    //以下两个均为 组织信息，根据type区分
    List<AddVO> orgs;//地市组织
    Long deptId;//部门组织

    private String nickname;
    private String eip;
    private String phone;
    private String email;
    private String avatar;


}
