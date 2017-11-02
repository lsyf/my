package com.loushuiyifan.report.controller.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportStorageService;
import com.loushuiyifan.report.service.ImportTaxService;
import com.loushuiyifan.system.vo.JsonResult;

/**
 * 财务报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("importDataTax")
public class ImportDataTaxController {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataTaxController.class);

    @Autowired
    DateService dateService;

    @Autowired
    ReportStorageService reportStorageService;

    @Autowired
    ImportTaxService importTaxService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }

    /**
     * 税务导入界面
     *
     * @return
     */
    @GetMapping
    public String index() {
        return "report/upload/importDataTax";
    }


    /**
     * 税务导入-导入
     *
     * @param file
     * @param month
     * @param remark
     * @param latnId
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String remark,
                             @ModelAttribute("user") User user) {

        Long userId = user.getId();

        //首先校验能否导入
        dateService.checkImportTax(month);

        //然后保存
        Path path = reportStorageService.store(file);

        //最后解析入库(失败则删除文件)
        try {
        	importTaxService.save(path, userId, month, remark);
        } catch (Exception e) {
            logger.error("6解析入库失败", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                logger.error("6删除文件失败", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }

    /**
     * 税务导入-查询
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        List<Map<String,Object>> list = importTaxService.list(month, userId);
                
        return JsonResult.success(list);
    }

    /**
     * 税务导入-删除
     *
     * @return
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(Long logId,
                             @ModelAttribute("user") User user) {
        Long userId = user.getId();
        importTaxService.delete(userId, logId);
        return JsonResult.success();
    }
}
