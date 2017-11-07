package com.loushuiyifan.report.dto;

import lombok.Data;

/**
 * 存储过程参数
 *
 * @author 漏水亦凡
 * @date 2017/9/26
 */
@Data
public class SPDataDTO implements Cloneable {
    //IN
    Long logId;
    Long userId;
    String month;

    //OUT返回值
    Integer rtnCode;
    String rtnMsg;

    @Override
    protected SPDataDTO clone() {
        SPDataDTO dto = null;
        try {
            dto = (SPDataDTO) super.clone();

            //清空返回值
            dto.setRtnCode(null);
            dto.setRtnMsg(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

}
