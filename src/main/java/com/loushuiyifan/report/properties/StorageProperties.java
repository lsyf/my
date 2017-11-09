package com.loushuiyifan.report.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@ConfigurationProperties("report.storage")
@Data
public class StorageProperties {

    //默认文件路径
    private String location;

    //图片
    private String image;

    //报表上传模板
    private String reportUploadTemplate;

    //报表上传(包括多种上传文件)
    private String reportUpload;

    //报表下载模板
    private String reportDownloadTemplate;

    //报表下载
    private String reportDownload;


}
