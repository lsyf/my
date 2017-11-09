package com.loushuiyifan.report.serv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author 漏水亦凡
 * @date 2017/9/21
 */
@Service
public class ImageStorageService extends DefaultStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);


    @Override
    public String configLocation() {
        return properties.getImage();
    }

    @Override
    public Path configPath(Path path, String name) throws IOException {
        return super.configPath(path, name);
    }
}
