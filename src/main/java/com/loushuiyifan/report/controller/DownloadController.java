package com.loushuiyifan.report.controller;

import com.loushuiyifan.report.exception.DownloadException;
import com.loushuiyifan.report.properties.StorageProperties;
import com.loushuiyifan.report.serv.DownloadService;
import com.loushuiyifan.system.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Controller
@RequestMapping("download")
public class DownloadController {

    @Autowired
    DownloadService downloadService;

    @Autowired
    StorageProperties storageProperties;

    @Autowired
    DictionaryService dictionaryService;

    //数据字典中 对应 模板字典的值
    public static final String TEMPLATE_DOWNLOAD = "templateDownload";

    /**
     * 下载 图片
     *
     * @param req
     * @param resp
     * @param name
     * @throws Exception
     */
    @GetMapping("image/{name:.+}")
    public void image(HttpServletRequest req,
                      HttpServletResponse resp,
                      @PathVariable String name) throws Exception {

        Path file = Paths.get(storageProperties.getImage(), name);
        downloadService.download(req, resp, file, name);
    }

    /**
     * 下载 导入模板
     *
     * @param req
     * @param resp
     * @param name
     * @throws Exception
     */
    @GetMapping("template/{name:.+}")
    public void template(HttpServletRequest req,
                         HttpServletResponse resp,
                         @PathVariable String name) throws Exception {

        //根据数据字典 查找对应模板的 文件名
        name = dictionaryService.getKidDataByName(TEMPLATE_DOWNLOAD, name);
        if (StringUtils.isEmpty(name)) {
            throw new DownloadException("未找到模板");
        }
        Path file = Paths.get(storageProperties.getReportTemplate(), name);
        downloadService.download(req, resp, file, name);
    }

}
