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
import com.loushuiyifan.report.service.ImportYccyService;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;
import com.loushuiyifan.system.vo.JsonResult;

/**
 * 业财差异
 * @author Administrator
 *
 */
@Controller
@RequestMapping("importYccy")
public class ImportYccyController {
	private static final Logger logger = LoggerFactory.getLogger(ImportYccyController.class);

	@Autowired
    DateService dateService;

    @Autowired
    ReportStorageService reportStorageService;

    @Autowired
    ImportYccyService importYccyService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }
    
    /**
     * 导入界面
     * @return
     */
    @GetMapping
    public String index() {
        return "report/upload/importYccy";
    }
    
    /**
     * 导入
     */
    @PostMapping("upload")
    @ResponseBody
    public JsonResult upload(@RequestParam("file") MultipartFile file,
                             String month,
                             String remark,
                             String latnId,
                             @ModelAttribute("user") User user) {

        Long userId = user.getId();

        //首先校验能否导入
        dateService.checkImportYccy(month);

        //然后保存
        Path path = reportStorageService.store(file);

        //最后解析入库(失败则删除文件)
        try {
        	importYccyService.save(path,
				                    userId,
				                    month,
				                    remark);
        } catch (Exception e) {
            logger.error("7解析入库失败", e);
            try {
                Files.delete(path);
            } catch (IOException e1) {
                logger.error("7删除文件失败", e1);
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
    public JsonResult list(String month, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        List<ImportLogDomTaxVO> list = importYccyService
                .list(userId, month);

        return JsonResult.success(list);
    }
    
    /**
     * 删除
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(Long logId,
                             @ModelAttribute("user") User user) {
        Long userId = user.getId();
        importYccyService.delete(Math.toIntExact(userId), logId);
        return JsonResult.success();
    }
    
}
