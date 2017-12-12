package com.loushuiyifan.config.shiro.realm;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.common.util.SpringUtil;
import com.loushuiyifan.config.shiro.tool.PasswordHelper;
import com.loushuiyifan.system.SystemException;
import com.loushuiyifan.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;


public class UserRealm extends AuthorizingRealm {


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        UserService userService = (UserService) SpringUtil.getBean("userService");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //暂不需要角色控制权限
        //authorizationInfo.setRoles(userService.findRoles(username));
        authorizationInfo.setStringPermissions(userService.findPermissions(username));
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        MyToken myToken = (MyToken) token;
        int type = myToken.getType();

        UserService userService = (UserService) SpringUtil.getBean("userService");
        User user = null;
        if (type == MyToken.TYPE_PASSWORD) {
            String username = (String) token.getPrincipal();
            user = userService.findByUsername(username);
        } else {
            String phone = myToken.getPhone();
            String tempPassword = "123";//设置通用密码用来兼容登录
            myToken.setPassword(tempPassword.toCharArray());

            PasswordHelper passwordHelper = (PasswordHelper) SpringUtil.getBean("passwordHelper");
            user = userService.findByPhone(phone);
            if (user == null) {
                throw new UnknownAccountException();//没找到帐号
            }
            user.setPassword(tempPassword);
            passwordHelper.encryptPassword(user);
        }
        //更新登录时间
        userService.updateLogin(user.getId());

        if (user == null) {
            throw new SystemException("用户不存在");
        }

        if (Boolean.TRUE.equals(user.getLocked())) {
            throw new SystemException("用户已锁定");
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getUsername() + user.getSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }


    @Override
    public String getAuthorizationCacheName() {
        return "authorizationCache_";
    }

    @Override
    public String getAuthenticationCacheName() {
        return "authenticationCache_";
    }
}
