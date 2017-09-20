package com.loushuiyifan.report;

import com.loushuiyifan.report.properties.StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class ReportConfig {
}
