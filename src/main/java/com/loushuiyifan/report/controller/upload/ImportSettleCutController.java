package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportSettleCutService;
import com.loushuiyifan.report.service.RptFundsFeeQueryService;
import com.loushuiyifan.report.service.RptSettleQueryService;
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

@Controller
@RequestMapping("importSettleCut")
public class ImportSettleCutController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportSettleCutController.class);


    @Autowired
    ImportSettleCutService importSettleCutService;
    @Autowired
    RptSettleQueryService rptSettleQueryService;

     @Autowired
     RptFundsFeeQueryService rptFundsFeeQueryService;

    /**
     * 结算数据切割页面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:importSettleCut:view")
    public String index(ModelMap map) {

        //页面条件
        List<CommonVO> months = dateService.lastMonths(0);
        List<Map<String, String>> orgs =rptSettleQueryService.listReportInfo();
        map.put("months", months);
        map.put("orgs", orgs);

        return "report/upload/importSettleCut";
    }
    
    /**
     * 文件导入
     * @param file
     * @param month
     * @param desc
     * @param user
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String remark,
                             @ModelAttribute("user") User user) {

        //首先校验能否导入
        dateService.checkImportSettCut(month);
        Long userId = user.getId();

        //存储
        Path path = reportStorageService.store(file);

        //解析入库(失败则删除文件)
        try {
        	importSettleCutService.save(path, month, remark, userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("4解析入库失败", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("4删除文件失败", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }

        return JsonResult.success();
    }


    /**
     * 查询
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listCut(String month,String reportId) {

    	Map<String, Object> map = importSettleCutService.queryList(month, reportId);

        return JsonResult.success(map);
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult delete(@ModelAttribute("user") User user, String month, Long logId) {
        dateService.checkImportSettCut(month);
        Long userId = user.getId();
        importSettleCutService.delete(userId, logId);
        return JsonResult.success();
    }


}
