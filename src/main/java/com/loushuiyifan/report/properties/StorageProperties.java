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
    private String location = "location";

    //图片
    private String image = "image";

    //报表模板
    private String reportTemplate = "reportTemplate";

    //报表上传(包括多种上传文件)
    private String reportUpload = "reportUpload";

    //报表下载
    private String reportDownload = "reportDownload";


}
