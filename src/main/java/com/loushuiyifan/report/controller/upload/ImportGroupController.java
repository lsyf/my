package com.loushuiyifan.report.controller.upload;

import java.nio.file.Path;

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
import com.loushuiyifan.report.serv.ReportStorageService;
import com.loushuiyifan.report.service.ImportGroupService;
import com.loushuiyifan.system.vo.JsonResult;

@Controller
@RequestMapping("importGroup")
public class ImportGroupController {
	private static final Logger logger = LoggerFactory.getLogger(ImportGroupController.class);

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
	 * @return
	 */
	@GetMapping
    public String index() {
        return "report/upload/importGroup";
    }
	
	/**
	 * 导入
	 * 
	 */
	@PostMapping("upload")
	@ResponseBody
	public JsonResult upload(@RequestParam("file") MultipartFile file,
			                 String month,
			                 String latnId,
			                 String remark,
			                 @ModelAttribute("user") User user){
		Long userId =user.getId();
		if (latnId.equals("0")) {
			throw new ReportException("请选择正确的地市");
		}
		 //然后保存
        Path path = reportStorageService.store(file);
		try {
			impportGroupService.save(path, month, Integer.parseInt(latnId), String.valueOf(userId), remark);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return JsonResult.success();	
	}
	
	
	/**
	 * 稽核
	 * 
	 */
	
	
	/**
	 * 删除
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
