package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportC5Service;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.vo.JsonResult;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 销售一线C5报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("importC5")
public class ImportC5Controller extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportC5Controller.class);


    @Autowired
    ImportC5Service importC5Service;


    /**
     * 销售一线C5导入界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:importC5:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC4(userId);
        List<CommonVO> months = dateService.lastMonths(2);

        map.put("orgs", orgs);
        map.put("months", months);

        return "report/upload/importC5";
    }


    /**
     * 销售一线C5导入-导入
     *
     * @param file
     * @param month
     * @param remark
     * @param latnId
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    //@RequiresPermissions("report:importC5:upload")
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String remark,
                             String latnId,
                             @ModelAttribute("user") User user) {

        //首先校验能否导入
        //TODO 测试后修改
        dateService.checkImportC5(month);

        Long userId = user.getId();

        //存储
        Path path = reportStorageService.store(file);

        //解析入库(失败则删除文件)
        try {
            importC5Service.save(path,
                    userId,
                    month,
                    Integer.parseInt(latnId),
                    remark);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("3解析入库失败", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("3删除文件失败", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }

    /**
     * 销售一线C5导入-稽核
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:importC5:view")
    public JsonResult listC5(String month, String latnId) {

        Map<String, Object> list = importC5Service.list(month, Integer.parseInt(latnId));

        return JsonResult.success(list);
    }


    /**
     * 销售一线C5导入-删除
     *
     * @return
     */
    @PostMapping("remove")
    @ResponseBody
    @RequiresPermissions("report:importC5:view")
    public JsonResult remove(Long logId, @ModelAttribute("user") User user) {
       
        Long userId = user.getId();
        importC5Service.delete(userId, logId);
        return JsonResult.success();
    }
}
