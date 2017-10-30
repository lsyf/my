package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataGroup;

import java.util.List;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportGroupDataDAO extends MyMapper<RptImportDataGroup> {

    void deleteGroup(Integer latnId, Long groupId);

    List<String> findSubcode(Long subcode);


}
