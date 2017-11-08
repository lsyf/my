package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class ImportLogDomTaxVO {
    Long logId;

    String fileName;

    String userId;

    String importDate;

    String status;

    Double sum;

    Integer count;
}
