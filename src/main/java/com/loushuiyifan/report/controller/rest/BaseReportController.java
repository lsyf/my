package com.loushuiyifan.report.controller.rest;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.serv.*;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Controller
public abstract class BaseReportController {
    @Autowired
    public DateService dateService;

    @Autowired
    public LocalNetService localNetService;

    @Autowired
    public CodeListTaxService codeListTaxService;

    @Autowired
    public ReportStorageService reportStorageService;

    @Autowired
    public ReportDownloadService reportDownloadService;

    @Autowired
    public DownloadService downloadService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }
}
