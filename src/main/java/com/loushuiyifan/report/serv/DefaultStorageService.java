package com.loushuiyifan.report.serv;

import com.loushuiyifan.report.exception.StorageException;
import com.loushuiyifan.report.properties.StorageProperties;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * 文件系统存储服务
 *
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Data
public abstract class DefaultStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStorageService.class);

    @Autowired
    protected StorageProperties properties;

    protected Path rootLocation;

    protected DateTimeFormatter nameSuffixFormatter
            = DateTimeFormatter.ofPattern("_yyyyMMddHHmmss.");


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

        //文件名后加时间戳
        String time = LocalDateTime.now().format(nameSuffixFormatter);
        name = FilenameUtils.getBaseName(name) + time + FilenameUtils.getExtension(name);
        return path.resolve(name);
    }

    @Override
    public abstract String configLocation();

    /**
     * 初始化存储目录
     */
    @Override
    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(configLocation());

            logger.debug("初始化文件存储目录: {}", rootLocation);

            if (!Files.exists(rootLocation)) {
                Files.createDirectory(rootLocation);
            }
        } catch (IOException e) {
            throw new StorageException("无法初始化存储服务", e);
        }
    }

    /**
     * 保存
     *
     * @param file
     * @return
     */
    @Override
    public Path store(MultipartFile file) {

        try {
            if (file.isEmpty()) {
                throw new StorageException("保存空文件失败: " + file.getOriginalFilename());
            }

            Path path = configPath(rootLocation, file.getOriginalFilename());
            Files.copy(file.getInputStream(), path);
            return path;
        } catch (IOException e) {
            throw new StorageException("保存文件失败: " + file.getOriginalFilename(), e);
        }
    }


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(path -> rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("读取文件失败", e);
        }

    }

    @Override
    public Path load(String path) {
        return Paths.get(path);
    }

    @Override
    public Resource loadAsResource(String path) {
        try {
            Path file = load(path);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("找不到文件: " + path);

            }
        } catch (MalformedURLException e) {
            throw new StorageException("无法读取文件: " + path, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }


}
