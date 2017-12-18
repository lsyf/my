package com.loushuiyifan.system.service;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.realm.MyToken;
import com.loushuiyifan.config.shiro.tool.PasswordHelper;
import com.loushuiyifan.system.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 漏水亦凡
 * @date 2017/12/8
 */
@Service
public class LoginService {


    @Autowired
    UserService userService;

    @Autowired
    HashedCredentialsMatcher credentialsMatcher;

    @Autowired
    PasswordHelper passwordHelper;

    @Autowired
    SendShortMsgService sendShortMsgService;

    public void login(String username,
                      String password,
                      String rememberMe,
                      String host) {
        Subject currentUser = SecurityUtils.getSubject();
        boolean flag = "on".equals(rememberMe);
        MyToken token = new MyToken(
                username, password, flag, host);
        currentUser.login(token);
    }

    public void loginByPhone(String username,
                             String host) {
        Subject currentUser = SecurityUtils.getSubject();
        MyToken token = new MyToken(username, host);
        currentUser.login(token);
    }

    /**
     * 发送验证码
     *
     * @return
     */
    public String sendPhoneCode(String username) {
        //首先验证账户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new SystemException("用户不存在");
        }
        String phone = user.getPhone();
        if (StringUtils.isEmpty(phone)) {
            throw new SystemException("该用户未配置手机号");
        }
        //然后发送验证码
        try {
            return sendShortMsgService.send(phone);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("发送验证码失败:" + e.getMessage());
        }

    }

    public void register(String username, String password) {
        User user = userService.findByUsername(username);
        if (user != null) {
            throw new SystemException("用户名已存在");
        }

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        passwordHelper.encryptPassword(user);

        userService.saveSelective(user);

    }

    public void passwd(String username, String old, String password) {
        //首先校验用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new SystemException("未查找到该用户，请重试");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getUsername() + user.getSalt()),//salt=username+salt
                ""  //realm name
        );

        AuthenticationToken token = new UsernamePasswordToken(username, old);

        //校验密码是否正确
        boolean flag = credentialsMatcher.doCredentialsMatch(token, info);
        if (!flag) {
            throw new SystemException("原密码错误");
        }

        //重新设置密码 并加密
        user.setPassword(password);
        passwordHelper.encryptPassword(user);

        int num = userService.updateNotNull(user);

    }


}
