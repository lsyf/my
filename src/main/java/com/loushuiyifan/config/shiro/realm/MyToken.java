package com.loushuiyifan.config.shiro.realm;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author 漏水亦凡
 * @date 2017/12/12
 */
@Data
public class MyToken extends UsernamePasswordToken {
    public static int TYPE_PASSWORD = 1;// 为密码登录
    public static int TYPE_PHONE = 2;// 为手机验证码登录
    private int type;

    public MyToken(String username, String password, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
        this.type = TYPE_PASSWORD;
    }

    public MyToken(String username, String host) {
        this(username, "123", false, host);
        this.type = TYPE_PHONE;
    }


}
