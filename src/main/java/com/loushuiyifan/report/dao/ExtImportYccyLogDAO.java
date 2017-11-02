package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.ExtImportYccyLog;

public interface ExtImportYccyLogDAO extends MyMapper<ExtImportYccyLog>{
	Long nextvalKey();
}
