package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author 漏水亦凡
 * @date 2017/11/16
 */
@Data
@Table(name = "rpt_itsm_log")
public class RptItsmLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select aweb_id.nextval from dual")
    Long logId;
    String acctMonth;
    Long userId;
    String requestBody;
    String responseBody;
    Date requestTime;
    Date responseTime;
    String resultCode;
    String itsmOrderNo;

    public RptItsmLog cloneWithId() {
        RptItsmLog log = new RptItsmLog();
        log.setLogId(this.logId);
        return log;
    }
}
