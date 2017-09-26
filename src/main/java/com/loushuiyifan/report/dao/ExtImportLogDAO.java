package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.ExtImportLog;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface ExtImportLogDAO extends MyMapper<ExtImportLog> {


    Long nextvalKey();

}
