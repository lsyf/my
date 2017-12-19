package com.loushuiyifan.system.controller;

import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.config.shiro.tool.PasswordHelper;
import com.loushuiyifan.system.service.DictionaryService;
import com.loushuiyifan.system.service.LoginService;
import com.loushuiyifan.system.service.UserService;
import com.loushuiyifan.system.vo.JsonResult;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 漏水亦凡
 * @create 2017-05-01 18:16.
 */
@Controller
public class LoginController {
    private Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    UserService userService;

    @Autowired
    HashedCredentialsMatcher credentialsMatcher;

    @Autowired
    PasswordHelper passwordHelper;

    @Autowired
    LoginService loginService;

    private static final String SYSTEM = "system";

    @GetMapping("/login")
    public String login() {
        String type = dictionaryService.getKidDataByName(SYSTEM, "loginType");
        //为2则进入短信登录界面
        if ("2".equals(type.trim())) {
            return "login2";
        }
        return "login";
    }

    @GetMapping("/login4admin")
    public String login4admin() {
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
    public JsonResult login(HttpServletRequest request, HttpServletResponse response,
                            String username,
                            String password,
                            String rememberMe) {
        String host = request.getRemoteHost();
        loginService.login(username, password, rememberMe, host);
        //登录成功自动跳转根目录
        String url = request.getContextPath();
        return JsonResult.success("登录成功", url);
    }

    @PostMapping("/loginByPhone")
    @ResponseBody
    public JsonResult loginByPhone(HttpServletRequest request, String username, String code) {
        String host = request.getRemoteHost();
        loginService.loginByPhone(username, code, host);
        String url = request.getContextPath();
        return JsonResult.success("登录成功", url);
    }

    @PostMapping("/sendPhoneCode")
    @ResponseBody
    public JsonResult sendPhoneCode(String username) {
        loginService.sendPhoneCode(username);
        return JsonResult.success();
    }


    @PostMapping("/register")
    @ResponseBody
    public JsonResult register(String username, String password) {
        loginService.register(username, password);
        return JsonResult.success();
    }

    @PostMapping("/passwd")
    @ResponseBody
    public JsonResult passwd(String username, String old, String password) {
        loginService.passwd(username, old, password);
        return JsonResult.success();
    }
}
