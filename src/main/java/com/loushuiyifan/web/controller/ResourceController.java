package com.loushuiyifan.web.controller;

import com.loushuiyifan.mybatis.bean.Resource;
import com.loushuiyifan.web.bean.DataPage;
import com.loushuiyifan.web.bean.JsonResult;
import com.loushuiyifan.web.service.ResourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-05 15:09.
 */
@Controller
@RequestMapping("resource")
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @GetMapping
    @RequiresPermissions("system:resource:view")
    public String index() {
        return "system/resource";
    }

    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("system:resource:view")
    public DataPage list(@RequestParam("offset") int offset,
                         @RequestParam("limit") int limit,
                         @RequestParam("type") String type) {

        DataPage dp= resourceService.getResource(offset, limit, type);

        return dp;
    }

    @PostMapping("list2")
    @ResponseBody
    @RequiresPermissions("system:resource:view")
    public List<Resource> list2(String type) {

        //不分页，查询所有结果
        List<Resource> resources = resourceService.getResources(type);

        return resources;
    }


    @PostMapping("update")
    @ResponseBody
    @RequiresPermissions("system:resource:update")
    public JsonResult update(String ids, int alls) {

        int num = resourceService.updateResource(ids, alls);

        return JsonResult.success(num);
    }

}
