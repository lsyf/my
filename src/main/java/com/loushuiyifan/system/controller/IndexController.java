package com.loushuiyifan.system.controller;

import com.loushuiyifan.system.service.IndexService;
import com.loushuiyifan.system.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * @author 漏水亦凡
 * @create 2017-03-17 17:22.
 */
@Controller
public class IndexController {
    private Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    IndexService indexService;

    @Autowired
    MenuService menuService;


    @GetMapping("/")
    public String index(Model model) {
        //此代码为不通过ajax而使用模板引擎拼装菜单
        //菜单列表
//        Sidebar sidebar = menuService.getSidebar("admin");
//        model.addAttribute("sidebar", sidebar);

        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }


    @GetMapping("/error")
    public String error(Model m) {
        m.addAttribute("error", "没有权限！");
        return "error";
    }


}
