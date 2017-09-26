package com.loushuiyifan.report.controller;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportStorageService;
import com.loushuiyifan.report.service.ImportIncomeDataService;
import com.loushuiyifan.system.vo.JsonResult;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 多种报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("import")
public class ImportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    DateService dateService;

    @Autowired
    ReportStorageService reportStorageService;

    @Autowired
    ImportIncomeDataService uploadIncomeDataService;

    /**
     * 收入导入界面
     *
     * @return
     */
    @GetMapping("incomeData")
    public String incomeData() {
        return "report/upload/incomeData";
    }


    /**
     * 收入导入-导入
     *
     * @param file
     * @param month
     * @param remark
     * @param latnId
     * @return
     */
    @PostMapping("incomeData")
    @ResponseBody
    public JsonResult file(@RequestParam("file") MultipartFile file,
                           String month, String remark, String latnId, HttpServletRequest request) {

        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        Long userId = user.getId();

        //首先校验能否导入
        dateService.checkUploadIncomeData(month);

        //然后保存
        Path path = reportStorageService.store(file);

        //最后解析入库
        try {
            uploadIncomeDataService.save(path,
                    Math.toIntExact(userId),
                    month,
                    Integer.parseInt(latnId),
                    remark);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析入库失败", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("删除文件失败", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }

}
