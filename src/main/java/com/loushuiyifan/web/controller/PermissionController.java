package com.loushuiyifan.web.controller;

import com.loushuiyifan.mybatis.bean.Permission;
import com.loushuiyifan.web.bean.JsonResult;
import com.loushuiyifan.web.service.PermissionService;
import com.loushuiyifan.web.service.ResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-02 10:33.
 */
@Controller
@RequestMapping("permission")
public class PermissionController {

    @Autowired
    PermissionService permissionService;
    @Autowired
    ResourceService resourceService;

    @GetMapping
    @RequiresPermissions("system:permission:view")
    public String index() {
        return "system/permission";
    }


    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("system:permission:view")
    public JsonResult list() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return JsonResult.success(permissions);
    }

    @PostMapping("/add")
    @ResponseBody
    @RequiresPermissions("system:permission:add")
    public JsonResult add(Permission permission) {
        int num = permissionService.addPermission(permission);
        return JsonResult.success(num);
    }

    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("system:permission:update")
    public JsonResult update(Permission permission) {
        int num = permissionService.updatePermission(permission);
        return JsonResult.success(num);
    }

    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("system:permission:delete")
    public JsonResult delete(@RequestBody Permission permission) {

        boolean isRelated = resourceService.isResourceRelatedRole(permission.getId(), "permission");

        if (isRelated) {
            return JsonResult.failure("该权限已被使用，无法删除");
        }

        int num = permissionService.deletePermission(permission);
        return JsonResult.success(num);
    }


}
