package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportIncomeDataService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.ImportDataLogVO;
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

/**
 * 财务报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("importIncomeData")
public class ImportIncomeDataController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportIncomeDataController.class);


    @Autowired
    ImportIncomeDataService importIncomeDataService;


    /**
     * 收入导入界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:importIncomeData:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC3(userId);
        List<CommonVO> months = dateService.commonMonths();

        map.put("orgs", orgs);
        map.put("months", months);

        return "report/upload/importIncomeData";
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
    @PostMapping("upload")
    @ResponseBody
    @RequiresPermissions("report:importIncomeData:upload")
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String remark,
                             String latnId,
                             @ModelAttribute("user") User user) {

        Long userId = user.getId();

        //首先校验能否导入
//        dateService.checkImportIncomeData(month);

        //然后保存
        Path path = reportStorageService.store(file);

        //最后解析入库(失败则删除文件)
        try {
            importIncomeDataService.save(path,
                    userId,
                    month,
                    Integer.parseInt(latnId),
                    remark);
        } catch (Exception e) {
            logger.error("1解析入库失败: ", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                logger.error("1删除文件失败: ", e1);
            } finally {
                throw new ReportException("1导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }

    /**
     * 收入导入-稽核
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:importIncomeData:view")
    public JsonResult list(String month, String latnId) {
        List<ImportDataLogVO> list = importIncomeDataService.list(latnId, month);

        return JsonResult.success(list);
    }

    /**
     * 收入导入-itsm送审
     */
    @PostMapping("itsm")
    @ResponseBody
    @RequiresPermissions("report:importIncomeData:itsm")
    public JsonResult itsm(@RequestParam("logIds[]") Long[] logIds,
                           @ModelAttribute("user") User user) {
        importIncomeDataService.itsm(logIds, user.getId());
        return JsonResult.success();
    }


    /**
     * 收入导入-提交
     */
    @PostMapping("commit")
    @ResponseBody
    @RequiresPermissions("report:importIncomeData:commit")
    public JsonResult commit(@RequestParam("logIds[]") Long[] logIds) {
        for (Long logId : logIds) {
            importIncomeDataService.commit(logId);
        }
        return JsonResult.success();
    }

    /**
     * 收入导入-删除
     *
     * @return
     */
    @PostMapping("remove")
    @ResponseBody
    @RequiresPermissions("report:importIncomeData:remove")
    public JsonResult remove(@RequestParam("logIds[]") Long[] logIds,
                             @ModelAttribute("user") User user) {
        Long userId = user.getId();
        for (Long logId : logIds) {
            importIncomeDataService.delete(userId, logId);
        }
        return JsonResult.success();
    }
}
