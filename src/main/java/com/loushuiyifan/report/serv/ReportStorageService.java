package com.loushuiyifan.report.serv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author 漏水亦凡
 * @date 2017/9/21
 */
@Service
public class ReportStorageService extends DefaultStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);


    /**
     * 配置报表存储根目录
     *
     * @return
     */
    @Override
    public String configLocation() {
        return properties.getReportUpload();
    }

    /**
     * 配置存储路径
     *
     * @param path
     * @param name
     * @return
     */
    @Override
    public Path configPath(Path path, String name) throws IOException {
        //根据日期分类存储
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        path = path.resolve(date);
        return super.configPath(path, name);
    }


}
