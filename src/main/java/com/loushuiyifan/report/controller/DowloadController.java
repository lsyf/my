package com.loushuiyifan.report.controller;

import com.loushuiyifan.report.properties.StorageProperties;
import com.loushuiyifan.report.service.DownloadService;
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
public class DowloadController {

    @Autowired
    DownloadService downloadService;

    @Autowired
    StorageProperties storageProperties;

    @GetMapping("image/{name:.+}")
    public void image(HttpServletRequest req,
                      HttpServletResponse resp,
                      @PathVariable String name) throws Exception {

        Path file = Paths.get(storageProperties.getImage(), name);
        downloadService.download(req, resp, file, name);
    }

    @GetMapping("template/{name:.+}")
    public void template(HttpServletRequest req,
                         HttpServletResponse resp,
                         @PathVariable String name) throws Exception {

        Path file = Paths.get(storageProperties.getReportTemplate(), name);
        downloadService.download(req, resp, file, name);
    }

}
