package com.loushuiyifan.report.controller;

import com.loushuiyifan.report.service.ImageStorageService;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    ImageStorageService storageService;


    @GetMapping
    public String index() {
        return "report/upload";
    }

    @PostMapping("file")
    @ResponseBody
    public JsonResult file(@RequestParam("file") MultipartFile file, String name) {

        storageService.store(file);
        return JsonResult.success(file.getOriginalFilename());
    }

}
