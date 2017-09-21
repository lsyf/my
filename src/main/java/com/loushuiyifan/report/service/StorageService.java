package com.loushuiyifan.report.service;

import com.loushuiyifan.report.properties.StorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/9/20.
 */
public interface StorageService {
    void init();

    Path store(MultipartFile file);

    default String configLocation(StorageProperties properties) {
        return properties.getLocation();
    }

    default Path configPath(Path path, String name) {
        return path.resolve(name);
    }

    Stream<Path> loadAll();

    Path load(String path);

    Resource loadAsResource(String path);

    void deleteAll();
}
