package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptCustDefChannel;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptCustDefChannelDAO extends MyMapper<RptCustDefChannel> {

    List<RptCustDefChannel> list(String rptNo);
}
