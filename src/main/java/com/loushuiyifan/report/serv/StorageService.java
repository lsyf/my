package com.loushuiyifan.report.serv;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/9/20.
 */
public interface StorageService {
    void init();

    Path store(MultipartFile file);

    String configLocation();

    default Path configPath(Path path, String name) throws IOException {
        return path.resolve(name);
    }

    Stream<Path> loadAll();

    Path load(String path);

    Resource loadAsResource(String path);

    void deleteAll();
}
