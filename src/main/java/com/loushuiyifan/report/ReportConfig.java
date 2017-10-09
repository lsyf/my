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
        MONTH_IMPORT_INCOME_DATA("MONTH_IMPORT_INCOME_DATA"),//财务导入账期限制
        TIME_IMPORT_INCOME_DATA("CWRPT_IMPORT_DEADLINE"),//财务导入时间限制
        MONTH_IMPORT_C5("CWRPT_IMPORT_C5"),//C5导入账期限制
        TIME_IMPORT_C5("CWRPT_IMPORT_DEADLINE_NEW");//C5导入时间限制

        private String data;

        RptAppParam(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

    /**
     * 导入报表类型
     */
    public enum RptImportType {
        INCOME_DATA("rpt_import_data_channel"),
        ICT("rpt_import_data_ict"),
        C5("rpt_import_data_c5");

        private String data;

        RptImportType(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }


}
