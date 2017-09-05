package com.loushuiyifan.system.controller;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.system.vo.JsonResult;
import com.loushuiyifan.system.service.OrganizationService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/6/19
 */
@Controller
@RequestMapping("organization")
public class OrganizationController {

    @Autowired
    OrganizationService orgnizationService;

    @GetMapping
    @RequiresPermissions("system:organization:view")
    public String index() {
        return "system/organization";
    }

    @PostMapping("listRoot")
    @RequiresPermissions("system:organization:view")
    @ResponseBody
    public JsonResult listRoot() {
        List<Organization> list = orgnizationService.listRoot(2);
        if (list == null || list.size() == 0) {
            return JsonResult.failure("无数据");
        }
        return JsonResult.success(list);
    }

    @PostMapping("listNodes")
    @RequiresPermissions("system:organization:view")
    @ResponseBody
    public JsonResult listNodes(Long id) {
        List<Organization> list = orgnizationService.listNodes(id);
        return JsonResult.success(list);
    }


    @PostMapping("listAll")
    @ResponseBody
    @RequiresPermissions("system:organization:view")
    public JsonResult listAll() {

        List<Organization> roles = orgnizationService.listAll();

        return JsonResult.success(roles);
    }

    @PostMapping("add")
    @RequiresPermissions("system:organization:add")
    @ResponseBody
    public JsonResult add(Organization org) {
        int num = orgnizationService.add(org);
        return JsonResult.success(org);
    }

    @PostMapping("update")
    @RequiresPermissions("system:organization:update")
    @ResponseBody
    public JsonResult update(Organization org) {
        int num = orgnizationService.update(org);
        return JsonResult.success(org);
    }

    @PostMapping("delete")
    @RequiresPermissions("system:organization:delete")
    @ResponseBody
    public JsonResult delete(Long id, String path) {

        boolean isRelated = orgnizationService.isOrgRelatedUser(id,path);

        if (isRelated) {
            return JsonResult.failure("该组织或下级已被使用，无法删除");
        }

        int num = orgnizationService.delete(id, path);
        return JsonResult.success(num);
    }


}
