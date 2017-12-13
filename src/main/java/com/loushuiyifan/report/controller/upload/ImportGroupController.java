package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.service.ImportGroupService;
import com.loushuiyifan.report.vo.ImportDataGroupVO;
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

@Controller
@RequestMapping("importGroup")
public class ImportGroupController extends BaseReportController {
    private static final Logger logger = LoggerFactory.getLogger(ImportGroupController.class);

    @Autowired
    ImportGroupService importGroupService;

    /**
     * 指标组配置页面
     *
     * @return
     */
    @GetMapping
    @RequiresPermissions("report:importCut:view")
    public String index(ModelMap map, @ModelAttribute("user") User user) {
        Long userId = user.getId();

        //页面条件
        List<Organization> orgs = localNetService.listForC4(userId);
        map.put("orgs", orgs);

        return "report/upload/importGroup";
    }

    /**
     * 导入
     */
    @PostMapping("upload")
    @ResponseBody
    @RequiresPermissions("report:importCut:view")
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String latnId,
                             @ModelAttribute("user") User user) {
        Long userId = user.getId();
       
        if (latnId.equals("0")) {
            throw new ReportException("请选择正确的地市");
        }

        //首先校验能否导入
        //TODO 测试修改
//        dateService.checkImportGroup();

        //然后保存
        Path path = reportStorageService.store(file);
        try {
            importGroupService.save(path,
                    Integer.parseInt(latnId),
                    userId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("5解析入库失败", e);

            try {
                Files.delete(path);
                //TODO 加入到事务中 导入失败删除数据库数据
                //importGroupService.delete(Integer.parseInt(latnId), Long.parseLong(groupId));
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("5删除文件失败", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }


    /**
     * 稽核
     */
    @PostMapping("list")
    @ResponseBody
    @RequiresPermissions("report:importCut:view")
    public JsonResult listGroup(String latnId,
                                String groupId) {

        List<ImportDataGroupVO> list = importGroupService.list(Integer.parseInt(latnId), groupId);

        return JsonResult.success(list);
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    @ResponseBody
    @RequiresPermissions("report:importCut:remove")
    public JsonResult remove(String latnId,
                             String groupId) {

//        if (latnId.equals("0")) {
//            throw new ReportException("请选择正确的地市");
//        }
        //首先校验能否导入 超过删除时限，禁止删除
        dateService.checkImportGroup();
        try {
            importGroupService.delete(Integer.parseInt(latnId), Long.parseLong(groupId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("删除导入时发生异常");

        }

        return JsonResult.success();
    }

}
