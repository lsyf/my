package com.loushuiyifan.system.controller;

import com.loushuiyifan.common.bean.Menu;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.system.vo.JsonResult;
import com.loushuiyifan.system.vo.Sidebar;
import com.loushuiyifan.system.service.MenuService;
import com.loushuiyifan.system.service.ResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-05-03 17:19.
 */
@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    MenuService menuService;
    @Autowired
    ResourceService resourceService;

    @GetMapping
    @RequiresPermissions("system:menu:view")
    public String index(Model model) {
        Sidebar sidebar = menuService.getSidebar("admin");
        model.addAttribute("sidebar", sidebar);
        return "system/menu";
    }

    @PostMapping("/sidebar")
    @ResponseBody
    public JsonResult sidebar(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        String username = user.getUsername();

        List<Menu> menus = menuService.getMenus(username);

        Map map = new HashMap();
        map.put("menus", menus);
        map.put("id", user.getId());
        map.put("username", username);

        return JsonResult.success(map);
    }

    @PostMapping("/list")
    @RequiresPermissions("system:menu:view")
    @ResponseBody
    public JsonResult list() {
        List<Menu> menus = menuService.getAllMenus();

        return JsonResult.success(menus);
    }


    @PostMapping("/add")
    @ResponseBody
    @RequiresPermissions("system:menu:add")
    public JsonResult add(Menu menu) {
        int num = menuService.addMenu(menu);
        return JsonResult.success(num);
    }

    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("system:menu:update")
    public JsonResult update(Menu menu) {
        int num = menuService.updateMenu(menu);
        return JsonResult.success(num);
    }

    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("system:menu:delete")
    public JsonResult delete(@RequestBody Menu menu) {

        boolean isRelated = resourceService.isResourceRelatedRole(menu.getId(), "menu");

        if (isRelated) {
            return JsonResult.failure("该菜单已被使用，无法删除");
        }

        int num = menuService.deleteMenu(menu);
        return JsonResult.success(num);
    }


}
