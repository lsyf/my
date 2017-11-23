package com.loushuiyifan.report.dao;

import java.util.List;
import java.util.Map;

public interface RptTaxExportDAO {
	List<Map<String, String>> queryTaxData(String month);
}
