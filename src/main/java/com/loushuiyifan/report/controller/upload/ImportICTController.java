package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportStorageService;
import com.loushuiyifan.report.service.ImportICTService;
import com.loushuiyifan.report.vo.ImportDataLogVO;
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
import java.util.List;

/**
 * ICT报表导入
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("importICT")
public class ImportICTController {
    private static final Logger logger = LoggerFactory.getLogger(ImportICTController.class);


    @Autowired
    ReportStorageService reportStorageService;

    @Autowired
    ImportICTService importICTService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }

    /**
     * ICT导入界面
     *
     * @return
     */
    @GetMapping
    public String index() {
        return "report/upload/importICT";
    }


    /**
     * ICT导入-导入
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
                             String latnId,
                             @ModelAttribute("user") User user) {

        Long userId = user.getId();

        //保存
        Path path = reportStorageService.store(file);

        //解析入库(失败则删除文件)
        try {
            importICTService.save(path,
                    userId,
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
                logger.error("删除文件失败 ", e1);
            } finally {
                throw new ReportException("导入失败: " + e.getMessage(), e);
            }
        }
        return JsonResult.success();
    }

    /**
     * ICT导入-稽核
     *
     * @param month
     * @return
     */
    @PostMapping("list")
    @ResponseBody
    public JsonResult list(String month, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        List<ImportDataLogVO> list = importICTService.list(userId, month);

        return JsonResult.success(list);
    }


    /**
     * ICT导入-删除
     *
     * @return
     */
    @PostMapping("remove")
    @ResponseBody
    public JsonResult remove(Long logId, @ModelAttribute("user") User user) {
        Long userId = user.getId();
        importICTService.delete(userId, logId);
        return JsonResult.success();
    }
}