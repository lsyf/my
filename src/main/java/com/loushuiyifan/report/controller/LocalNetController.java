package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.service.LocalNetService;
import com.loushuiyifan.system.vo.JsonResult;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据用户 获取本地网列表
     * @param lvl
     * @param request
     * @return
     */
    @PostMapping("listByUser")
    public JsonResult listByUser(Integer lvl,HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        Long userId = user.getId();

        List<Map> list = localNetService.listByUser(userId,lvl);
        return JsonResult.success(list);
    }

}
