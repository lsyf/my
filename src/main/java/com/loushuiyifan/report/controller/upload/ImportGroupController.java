package com.loushuiyifan.report.controller.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
import com.loushuiyifan.report.service.ImportGroupService;
import com.loushuiyifan.report.vo.ImportDataGroupVO;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("importGroup")
public class ImportGroupController {
    private static final Logger logger = LoggerFactory.getLogger(ImportGroupController.class);

    @Autowired
    DateService dateService;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    ImportGroupService impportGroupService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }

    /**
     * 指标组配置页面
     *
     * @return
     */
    @GetMapping
    public String index() {
        return "report/upload/importGroup";
    }

    /**
     * 导入
     */
    @PostMapping("upload")
    @ResponseBody
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String latnId,
                             String groupId,
                             @ModelAttribute("user") User user) {
        Long userId = user.getId();
        if (latnId.equals("0")) {
            throw new ReportException("请选择正确的地市");
        }
        //首先校验能否导入
        dateService.checkImportGroup(month);
        //然后保存
        Path path = reportStorageService.store(file);
        try {
            impportGroupService.save(path, month, Integer.parseInt(latnId), String.valueOf(userId), groupId);
        } catch (Exception e) {
          			
        	e.printStackTrace();
            logger.error("5解析入库失败", e);
            try {
                Files.delete(path);
 
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
     *
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult listGroup(String latnId,
    							String groupId, 
    							@ModelAttribute("user") User user) {
        Long userId = user.getId();
        //TODO 权限不足,非本地导入员无法查询其他地市导入数据
        //groupId 指标组编码若为空，则按本地网查询
        List<ImportDataGroupVO> list =impportGroupService
        		.list(Integer.parseInt(latnId), Long.parseLong(groupId));

        return JsonResult.success(list);
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(String latnId,
    		                 String groupId,
    		                 String month
                             ) {
        
        if (latnId.equals("0")) {
            throw new ReportException("请选择正确的地市");
        }
        //首先校验能否导入
        dateService.checkImportGroup(month);
        //groupId 指标组编码若为空，则按本地网删除
        try {
        	impportGroupService.delete(Integer.parseInt(latnId), Long.parseLong(groupId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("删除导入时发生异常");
			
		}
        
        return JsonResult.success();
    }

}
