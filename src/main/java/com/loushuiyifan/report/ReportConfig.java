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

    public static final String ITSM_ACCOUNT = "sdq.zj";

    /**
     * 报表参数
     */
    public enum RptAppParam {
        ROOT("RptAppParam"),//参数表root
        MONTH_IMPORT_INCOME_DATA("MONTH_IMPORT_INCOME_DATA"), //财务导入账期限制
        TIME_IMPORT_INCOME_DATA("TIME_IMPORT_INCOME_DATA"),   //财务导入时间限制
        MONTH_IMPORT_C5("MONTH_IMPORT_C5"),      //C5导入账期限制
        TIME_IMPORT_C5("TIME_IMPORT_C5"),        //C5导入时间限制
        MONTH_IMPORT_CUT("MONTH_IMPORT_CUT"),    //切割比例导入账期限制
        TIME_IMPORT_CUT("TIME_IMPORT_CUT"),     //切割比例导入时间限制        
        TIME_IMPORT_GROUP("TIME_IMPORT_GROUP"),      //指标组导入时间限制
        MONTH_IMPORT_TAX("MONTH_IMPORT_TAX"),    //税务导入账期限制
        TIME_IMPORT_TAX("TIME_IMPORT_TAX"),      //税务导入时间限制
        MONTH_SETTLE_CUT("MONTH_SETTLE_CUT"),    //集团切割导入账期限制
        TIME_SETTLE_CUT("TIME_SETTLE_CUT"),     //集团切割导入时间限制   
        TIME_UPDATE_PROCESS("TIME_UPDATE_PROCESS"); //修改收入来源完成度状态
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
        C5("rpt_import_data_c5"),
        CUT("rpt_import_data_cut"),
        TAX("rpt_import_data_tax"),
        GROUP("rpt_import_data_group");
        private String data;

        RptImportType(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

    /**
     * 导出报表模板
     */
    public enum RptExportType {
        PARENT("reportDownloadTemplate"),
        RPT_QUERY_CUST("rpt_query_cust"),
        RPT_QUERY_INCOME_SOURCE("rpt_query_incomeSouce"),
        RPT_QUERY_COMPARED_NUM("rpt_query_comeparedNum"),
        RPT_QUERY_COM_DETAIL("rpt_query_comDetail");

        private String data;

        RptExportType(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

    public static String getTaxType(String type) {
        switch (type) {
            case "0":
                type = "价税合一(含税)";
                break;
            case "1":
                type = "增值税";
                break;
            case "2":
                type = "价(不含税)";
                break;
        }
        return type;
    }

}
