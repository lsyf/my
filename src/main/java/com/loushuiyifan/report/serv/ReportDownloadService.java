package com.loushuiyifan.report.serv;

import com.loushuiyifan.report.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author 漏水亦凡
 * @date 2017/9/21
 */
@Service
public class ReportDownloadService extends DefaultStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);


    /**
     * 配置报表下载根目录
     *
     * @return
     */
    @Override
    public String configLocation() {
        return properties.getReportDownload();
    }

    /**
     * 配置报表模板目录
     *
     * @return
     */
    public String configTemplateLocation() {
        return properties.getReportDownloadTemplate();
    }


    /**
     * 配置存储路径 和 文件名称规则
     *
     * @param path
     * @param name
     * @return
     */
    @Override
    public Path configPath(Path path, String name) throws IOException {
        //如果目录不存在则创建
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.resolve(name);
    }


    public Path store(File file, String month, String name) {
        try {
            Path path = configPath(rootLocation.resolve(month), name);
            Files.copy(new FileInputStream(file), path);
            return path;
        } catch (IOException e) {
            throw new StorageException("保存文件失败: " + name, e);
        }
    }


}
