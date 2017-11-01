package com.loushuiyifan.report.controller.upload;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.config.shiro.ShiroConfig;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportStorageService;
import com.loushuiyifan.report.service.ImportCutService;
import com.loushuiyifan.report.vo.CutDataListVO;
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

@Controller
@RequestMapping("importCut")
public class ImportCutController {
    private static final Logger logger = LoggerFactory.getLogger(ImportCutController.class);

    @Autowired
    DateService dateService;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    ImportCutService importCutService;

    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        HttpSession session = WebUtils.toHttp(request).getSession();
        User user = (User) session.getAttribute(ShiroConfig.SYS_USER);
        return user;
    }

    /**
     * 切割比例配置页面
     *
     * @return
     */
    @GetMapping
    public String index() {
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
                             String shareType,
                             String remark,
                             @ModelAttribute("user") User user) {

        //首先校验能否导入
        dateService.checkImportCut(month);
        String userName = user.getUsername();
        //TODO  用户所在地市判断
        //存储
        Path path = reportStorageService.store(file);

        //解析入库(失败则删除文件)
        try {
            importCutService.save(path,
                    month,
                    latnId,
                    incomeSource,
                    shareType,
                    userName,
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
