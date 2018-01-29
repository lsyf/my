package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportTaxService;
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
 * 税务报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("importDataTax")
public class ImportDataTaxController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataTaxController.class);


    @Autowired
    ImportTaxService importTaxService;


    /**
     * 税务导入界面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:importDataTax:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<CommonVO> months = dateService.calcMonths(1,2);
        map.put("months", months);

        return "report/upload/importDataTax";
    }


    /**
     * 税务导入-导入
     *
     * @param file
     * @param month
     * @param remark
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    @RequiresPermissions("report:importDataTax:view")
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
    @RequiresPermissions("report:importDataTax:view")
    public JsonResult list(String month, @ModelAttribute("user") User user) {
        Long userId = user.getId();
      
        Map<String, Object> list = importTaxService.list(month, userId);

        return JsonResult.success(list);
    }

    /**
     * 税务导入-删除
     *
     * @return
     */
    @PostMapping("remove")
    @ResponseBody
    @RequiresPermissions("report:importDataTax:view")
    public JsonResult remove(Long logId,
                             @ModelAttribute("user") User user) {
       
        Long userId = user.getId();
        try {
            importTaxService.delete(userId, logId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }

    /**
     * 税务导入-生成税务
     *
     * @return
     */
    @PostMapping("createTax")
    @ResponseBody
    @RequiresPermissions("report:importDataTax:view")
    public JsonResult createTaxData(String month) {
        //TODO  未测试
        //TODO 待修改 存过  IRPT_DEL_TAXDATA

        try {
            importTaxService.taxFile(month, "007");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success();
    }

}
