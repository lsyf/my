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

    /**
     * 报表参数
     */
    public enum RptAppParam {
        ROOT("RptAppParam"),//参数表root
        TIME_FOR_IMPORT_DATA("TIME_FOR_IMPORT_DATA"),//导入账期限制
        IMPORT_INCOME_DATA("CWRPT_IMPORT_DEADLINE");//导入时间限制

        private String data;

        RptAppParam(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }


}
