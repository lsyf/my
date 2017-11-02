package com.loushuiyifan.report.controller.rest;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.system.vo.JsonResult;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 本地网（地市）控制
 *
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@RestController
@RequestMapping("localNet")
public class LocalNetController {

    @Autowired
    LocalNetService localNetService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }

    /**
     * 根据用户 获取本地网列表
     */
    @PostMapping("listAllByUser")
    public JsonResult listAllByUser(Integer lvl, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        List<Organization> list = localNetService.listAllByUser(userId, lvl);
        return JsonResult.success(list);
    }

    /**
     * 根据用户 获取仅限股份下列表
     */
    @PostMapping("listForC5")
    public JsonResult listForC5(@ModelAttribute("user") User user) {
        Long userId = user.getId();

        List<Organization> list = localNetService.listForC5(userId);
        return JsonResult.success(list);
    }

}
