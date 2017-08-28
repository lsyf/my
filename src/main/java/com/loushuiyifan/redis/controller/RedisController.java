package com.loushuiyifan.redis.controller;

import com.loushuiyifan.redis.bean.RedisKey;
import com.loushuiyifan.redis.service.RedisService;
import com.loushuiyifan.web.bean.JsonResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-25 20:31.
 */
@Controller
@RequestMapping("redis")
public class RedisController {

    @Autowired
    RedisService redisService;

    @GetMapping
    @RequiresPermissions("system:redis:view")
    public String index() {
        return "/system/redis";
    }

    @PostMapping("list")
    @RequiresPermissions("system:redis:view")
    @ResponseBody
    public List<RedisKey> list(String key) {
        List<RedisKey> list = redisService.list(key);
        return list;
    }

    @PostMapping("info")
    @RequiresPermissions("system:redis:view")
    @ResponseBody
    public JsonResult info(String key) {
        RedisKey redisKey = redisService.info(key);
        return JsonResult.success(redisKey);
    }

    @PostMapping("delete")
    @RequiresPermissions("system:redis:delete")
    @ResponseBody
    public JsonResult delete(@RequestParam(value = "keys[]", required = false) String[] keys) {
        int num = redisService.delete(keys);
        return JsonResult.success(num);
    }

}
