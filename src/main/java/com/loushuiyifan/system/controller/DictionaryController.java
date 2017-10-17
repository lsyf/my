package com.loushuiyifan.system.controller;

import com.loushuiyifan.common.bean.Dictionary;
import com.loushuiyifan.system.vo.JsonResult;
import com.loushuiyifan.system.service.DictionaryService;
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
@RequestMapping("dictionary")
public class DictionaryController {

    @Autowired
    DictionaryService dictionaryService;

    @GetMapping
    @RequiresPermissions("system:dictionary:view")
    public String index() {
        return "system/dictionary";
    }

    @PostMapping("listRoot")
    @RequiresPermissions("system:dictionary:view")
    @ResponseBody
    public JsonResult listRoot(String name) {
        List<Dictionary> list = dictionaryService.listRoot(name);
        if (list == null || list.size() == 0) {
            return JsonResult.failure("数据为空");
        }
        return JsonResult.success(list);
    }

    @PostMapping("listNodes")
    @RequiresPermissions("system:dictionary:view")
    @ResponseBody
    public JsonResult listNodes(Long id) {
        List<Dictionary> list = dictionaryService.listNodes(id);
        return JsonResult.success(list);
    }


    @PostMapping("add")
    @RequiresPermissions("system:dictionary:add")
    @ResponseBody
    public JsonResult add(Dictionary dic) {
        int num = dictionaryService.add(dic);
        return JsonResult.success(dic);
    }

    @PostMapping("update")
    @RequiresPermissions("system:dictionary:update")
    @ResponseBody
    public JsonResult update(Dictionary dic) {
        int num = dictionaryService.update(dic);
        return JsonResult.success(dic);
    }

    @PostMapping("delete")
    @RequiresPermissions("system:dictionary:delete")
    @ResponseBody
    public JsonResult delete(Long id) {
        int num = dictionaryService.delete(id);
        return JsonResult.success(num);
    }

    @PostMapping("get")
    @RequiresPermissions("system:dictionary:delete")
    @ResponseBody
    public JsonResult getParameter(String name) {
        List<Dictionary> list = dictionaryService.getAllKidsByData(name);
        return JsonResult.success(list);
    }


}
