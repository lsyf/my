package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportCutService;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.report.vo.CutDataListVO;
import com.loushuiyifan.system.vo.JsonResult;
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
@RequestMapping("importCut")
public class ImportCutController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportCutController.class);


    @Autowired
    ImportCutService importCutService;


    /**
     * 切割比例配置页面
     *
     * @return
     */
    @GetMapping
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC4(userId);
        List<CommonVO> months = dateService.aroundMonths(5);
        List<Map> incomeSources = codeListTaxService.listByType("income_source2017");

        map.put("orgs", orgs);
        map.put("months", months);
        map.put("incomeSources", incomeSources);

        return "report/upload/importCut";
    }

    /**
     * 文件导入
     *
     * @param file
     * @param month
     * @param latnId
     * @param incomeSource
     * @param remark
     * @param user
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String latnId,
                             String incomeSource,
                             String cutType,
                             String remark,
                             @ModelAttribute("user") User user) {

        //首先校验能否导入
        dateService.checkImportCut(month);
        String username = user.getUsername();

        //存储
        Path path = reportStorageService.store(file);

        //解析入库(失败则删除文件)
        try {
            importCutService.save(path,
                    month,
                    Integer.parseInt(latnId),
                    incomeSource,
                    Integer.parseInt(cutType),
                    username,
                    remark);
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
    @PostMapping("listCut")
    @ResponseBody
    public JsonResult listCut(
            String month,
            String latnId,
            String incomeSource,
            String shareType,
            String remark,
            @ModelAttribute("user") User user) {
        //校验账期，本地网，收入来源不能为空
        // 地市id 用作权限控制
        Long userId = user.getId();

        List<CutDataListVO> list = importCutService.queryList(month, Integer.parseInt(latnId),
                incomeSource, Integer.parseInt(shareType));
        //Map<String, Object> result = new HashMap<String, Object>();
        //result.put("l_fail_file", list);
        return JsonResult.success(list);
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult delete(@ModelAttribute("user") User user,
                             String month,
                             Integer latnId,
                             String incomeSource,
                             Integer shareType) {
        String userName = user.getUsername();
        importCutService.delete(month, latnId, incomeSource, shareType, userName);
        return JsonResult.success();
    }


}
