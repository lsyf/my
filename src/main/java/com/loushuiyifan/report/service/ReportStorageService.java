package com.loushuiyifan.report.service;

import com.loushuiyifan.report.properties.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    public ReportStorageService(StorageProperties properties) {
        super(properties);
    }

    @Override
    public String configLocation(StorageProperties properties) {
        return properties.getReportUpload();
    }

    @Override
    public Path configPath(Path path, String name) {
        //根据日期存储
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        path = path.resolve(date);
        return super.configPath(path, name);
    }


}
