package com.loushuiyifan.da.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/6/6
 */
@Data
public class Report2 {

    private String month;//月份
    private Double mobile;//移动
    private Double fixNetwork;//固网

    private List<Map<String,Object>> mobileProduct;
    private List<Map<String,Object>> fixNetworkProduct;


}
