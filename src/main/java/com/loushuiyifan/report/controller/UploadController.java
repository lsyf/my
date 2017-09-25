package com.loushuiyifan.report.controller;

import com.loushuiyifan.report.service.ReportStorageService;
import com.loushuiyifan.report.service.UploadIncomeDataService;
import com.loushuiyifan.system.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    ReportStorageService reportStorageService;

    @Autowired
    UploadIncomeDataService uploadIncomeDataService;

    /**
     * 收入导入界面
     *
     * @return
     */
    @GetMapping("incomeData")
    public String incomeData() {
        return "report/upload/incomeData";
    }

    @PostMapping("incomeData")
    @ResponseBody
    public JsonResult file(@RequestParam("file") MultipartFile file,
                           String month, String remark, String latnId) {
        Path path = reportStorageService.store(file);
        uploadIncomeDataService.save(path);
        return JsonResult.success();
    }

}
