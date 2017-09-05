package com.loushuiyifan.system.controller;

import com.loushuiyifan.common.bean.Role;
import com.loushuiyifan.system.vo.JsonResult;
import com.loushuiyifan.system.vo.RoleVO;
import com.loushuiyifan.system.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-05-05 15:09.
 */
@Controller
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping
    @RequiresPermissions("system:role:view")
    public String index() {
        return "system/role";
    }


    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("system:role:view")
    public List<Role> list(String name) {

        List<Role> roles = roleService.getRoles(name);

        return roles;
    }

    @PostMapping("listAll")
    @ResponseBody
    @RequiresPermissions("system:role:view")
    public JsonResult listAll() {

        List<Role> roles = roleService.listAll();

        return JsonResult.success(roles);
    }

    @PostMapping("info")
    @ResponseBody
    @RequiresPermissions("system:role:view")
    public JsonResult info(String id) {

        Map<String, Object> map = roleService.getRoleInfo(id);

        return JsonResult.success(map);
    }

    @PostMapping("mp")
    @ResponseBody
    @RequiresPermissions("system:role:view")
    public JsonResult AllMP() {

        Map<String, Object> map = roleService.getAllMP();

        return JsonResult.success(map);
    }

    @PostMapping("update")
    @ResponseBody
    @RequiresPermissions("system:role:update")
    public JsonResult update(@RequestBody RoleVO roleUpdate) {

        int num = roleService.updateRole(roleUpdate);

        return JsonResult.success(num);
    }

    @PostMapping("add")
    @ResponseBody
    @RequiresPermissions("system:role:add")
    public JsonResult add(@RequestBody RoleVO roleUpdate) {

        int num = roleService.addRole(roleUpdate);

        return JsonResult.success(num);
    }

    @PostMapping("delete")
    @ResponseBody
    @RequiresPermissions("system:role:delete")
    public JsonResult delete(long id) {

        boolean isRelated = roleService.isRoleRelatedUser(id);

        if (isRelated) {
            return JsonResult.failure("该角色已被使用，无法删除");
        }

        int num = roleService.deleteRole(id);

        return JsonResult.success(num);
    }
}
