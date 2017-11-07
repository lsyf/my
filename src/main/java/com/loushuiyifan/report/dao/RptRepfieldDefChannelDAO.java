package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptRepfieldDefChannel;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptRepfieldDefChannelDAO extends MyMapper<RptRepfieldDefChannel> {

    List<RptRepfieldDefChannel> list(String rptNo);

    List<Map> listMap(String rptNo);

}
