package com.loushuiyifan.web.controller;

import com.loushuiyifan.mybatis.bean.User;
import com.loushuiyifan.mybatis.service.UserService;
import com.loushuiyifan.shiro.ShiroConfig;
import com.loushuiyifan.shiro.tool.PasswordHelper;
import com.loushuiyifan.web.bean.JsonResult;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 漏水亦凡
 * @create 2017-05-01 18:16.
 */
@Controller
public class LoginController {
    private Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    UserService userService;

    @Autowired
    HashedCredentialsMatcher credentialsMatcher;

    @Autowired
    PasswordHelper passwordHelper;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/passwd")
    public String passwd(HttpServletRequest request, Model model) {
        String username = (String) request.getAttribute(ShiroConfig.SYS_USERNAME);
        model.addAttribute("username", username);
        return "passwd";
    }


    @PostMapping("/login")
    @ResponseBody
    public JsonResult login(HttpServletRequest req) {
        String msg = null;
        Object data = null;

        //登录处理信息
        String exceptionClassName = (String) req.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);

        if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            msg = "用户名不存在";
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            msg = "用户名/密码错误";
        } else if (exceptionClassName != null) {
            msg = "其他错误: " + exceptionClassName;
        } else {
            data = req.getAttribute("successUrl");
            return JsonResult.success(data);
        }

        return JsonResult.failure(msg);
    }


    @PostMapping("/register")
    @ResponseBody
    public JsonResult register(String username, String password) {

        User user = userService.findByUsername(username);
        if (user != null) {
            return JsonResult.failure("用户名已存在");
        }

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        passwordHelper.encryptPassword(user);

        userService.save(user);


        return JsonResult.success();
    }

    @PostMapping("/passwd")
    @ResponseBody
    public JsonResult passwd(String username, String old, String password) {

        //首先校验用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            return JsonResult.failure("未查找到该用户，请重试");
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
            return JsonResult.failure("原密码错误");
        }

        //重新设置密码 并加密
        user.setPassword(password);
        passwordHelper.encryptPassword(user);

        int num = userService.updateNotNull(user);

        return JsonResult.success();
    }
}
