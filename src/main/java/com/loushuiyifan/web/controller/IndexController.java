package com.loushuiyifan.web.controller;

import com.loushuiyifan.web.bean.JsonResult;
import com.loushuiyifan.web.service.IndexService;
import com.loushuiyifan.web.service.MenuService;
import com.loushuiyifan.web.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


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

    @Autowired
    UploadService uploadService;

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

    @RequestMapping(value = "/watermark", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult watermark(
            @RequestParam("image") MultipartFile file, HttpSession session) throws Exception {
        String uploadPath = "/pic";
        String realUploadPath = "D:/pic";
        String imageUrl = uploadService.uploadImage(file, uploadPath, realUploadPath);
        String logoImageUrl = uploadService.watermark(file, uploadPath, realUploadPath);

        Map map = new HashMap();
        map.put("imageUrl", imageUrl);
        map.put("logoImageUrl", logoImageUrl);

        return JsonResult.success(map);
    }


}
