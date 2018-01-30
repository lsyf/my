package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.util.ProtoStuffSerializerUtil;
import com.loushuiyifan.config.poi.PoiUtils;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ReportCache;
import com.loushuiyifan.report.bean.RptCase;
import com.loushuiyifan.report.dao.ReportCacheDAO;
import com.loushuiyifan.report.dao.RptQueryComDetailDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.*;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.service.DictionaryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Service
public class RptQueryComDetailService {

	private static Logger logger = LoggerFactory.getLogger(RptQueryComDetailService.class);

	@Autowired
	RptEditionService rptEditionService;

	@Autowired
	RptQueryComDetailDAO rptQueryComDetailDAO;

	@Autowired
	LocalNetService localNetService;

	@Autowired
	DictionaryService dictionaryService;

	@Autowired
	ReportDownloadService reportDownloadService;

	@Autowired
	RptCaseService rptCaseService;

	@Autowired
	ReportCacheDAO reportCacheDAO;

	// 生成缓存状态
	ConcurrentHashMap<String, Instant> cacheStatusMap = new ConcurrentHashMap<>(3000);

	@Transactional
	public RptQueryDataVO list(String month, String latnId, String type, Long userId) {

		// 查询报表实例是否存在
		// case(状态不为2)和cache同步，两者皆有或者两者皆无
		RptCase rptCase = rptCaseService.selectRptComDetailCase(month, latnId, type);
		if (rptCase != null) {
			Long rptCaseId = rptCase.getRptCaseId();
			ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCaseId);
			byte[] data = cache.getHtmlData();
			RptQueryDataVO vo = ProtoStuffSerializerUtil.deserialize(data, RptQueryDataVO.class);
			return vo;

		}

		// 判断是否正在生成
		String cacheStatusKey = month + latnId + type;
		Instant lastInst = cacheStatusMap.get(cacheStatusKey);
		if (lastInst != null) {
			Long during = Duration.between(lastInst, Instant.now()).getSeconds();
			throw new ReportException("正在生成数据，已耗时: " + during + "秒");
		}
		// 设置为正在生成缓存状态
		cacheStatusMap.put(cacheStatusKey, Instant.now());
		RptQueryDataVO vo = new RptQueryDataVO();

		try {
			rptCase = new RptCase();
			rptCase.setAcctMonth(month);
			rptCase.setLatnId(Long.parseLong(latnId));
			rptCase.setProcessId(1101L);
			rptCase.setReportNo(1801L);
			rptCase.setCreateUserid(userId + "");
			rptCase.setTaxMark(Integer.parseInt(type));
			rptCase.setType(ReportConfig.RptExportType.RPT_QUERY_COM_DETAIL.toString());// 类型
			Long rptCaseId = rptCaseService.saveCaseSelective(rptCase);

			Long start = System.currentTimeMillis();
			// 列名
			List<Map<String, String>> cols = rptEditionService.listComDetailColMap();			
			// 指标
			List<Map<String, String>> fields = rptEditionService.listComDetailRowMap();
			// 数据
			Map<String, Map<String, String>> datas = getDataMap(month, latnId);
			if (datas == null || datas.size() == 0) {
				throw new ReportException("数据还未准备好！");
			}

			// 由于fields接下来会更改，优先生成文件
			List<Map<String, String>> listcol =rptQueryComDetailDAO.selectIndexCodeAndName("1801");
			String filePath = export(month, latnId, type, listcol, fields, datas);

			// 生成文件后sftp推送到文件主机
			FileService.push(filePath);

			// 生成html需求数据模型
			// 首先遍历指标,建立 id->feild
			Map<String, Map<String, String>> fieldMap = Maps.newHashMapWithExpectedSize(2000);
			for (int i = 0; i < fields.size(); i++) {
				Map<String, String> temp = fields.get(i);
				fieldMap.put(temp.get("id"), temp);
			}

			// 遍历数据，分别插入到指标数据中			
			Iterator<String> it = datas.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String[] xy = key.split("_", 2);
				String x = xy[0];
				String y = xy[1];
				String v = datas.get(key).get("data");

				Map<String, String> temp = fieldMap.get(x);
				if (temp == null) {
					continue;
				}
				temp.put(y, v);
			}

			
			Long end = System.currentTimeMillis();
			logger.info("查询时间：" + (end - start) / 1000 + "秒");

			vo.setTitles(cols);
			vo.setDatas(fields);

			// 保存缓存
			rptCaseService.saveReportCache(rptCaseId, ProtoStuffSerializerUtil.serialize(vo), filePath);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("：" + e.getMessage());
		} finally {
			// 移除生成状态
			cacheStatusMap.remove(cacheStatusKey);
		}
		return vo;
	}

	public Map<String, Map<String, String>> getDataMap(String month, String latnId) {
		LocalDate thisMonth = LocalDate.parse(month + "01", DateService.YYYYMMDD);
		String firstMonth = thisMonth.with(TemporalAdjusters.firstDayOfYear()).format(DateService.YYYYMM);
		String lastYearThisMonth = thisMonth.plusYears(-1).format(DateService.YYYYMM);

		Map<String, Map<String, String>> list_lastYear = rptQueryComDetailDAO.lastYearAsMap(lastYearThisMonth, latnId);
		Map<String, Map<String, String>> list_thisMonth = rptQueryComDetailDAO.thisMonthAsMap(month, latnId);
		Map<String, Map<String, String>> list_thisYear = rptQueryComDetailDAO.thisYearAsMap(firstMonth, month, latnId);

		Map<String, Map<String, String>> map = Maps.newHashMap();
		map.putAll(list_lastYear);
		map.putAll(list_thisMonth);
		map.putAll(list_thisYear);
		
		return map;
	}

	/**
	 * 根据已获取的数据生成文件
	 */
	public String export(String month, String latnId, String type, 
						List<Map<String, String>> cols,
						List<Map<String, String>> fields, 
						Map<String, Map<String, String>> data) throws Exception {
		String latnName = localNetService.getCodeName(latnId);
		if (latnName == null) {
			throw new ReportException("选择营业区错误,请重试");
		}

		// 数据
		LinkedHashMap reportData = new LinkedHashMap<>();
		reportData.put(latnName, data);

		String templatePath = configTemplatePath();
		String outPath = configExportPath(month, latnName, type, false);

		ReportExportServ export = new RptQueryComDetailExport();
		export.template(templatePath).out(outPath).column(cols).row(fields).data(reportData).export();

		return outPath;
	}

	public String export(String month, String latnId, String type, Boolean isMulti) throws Exception {

		// 非多营业区 可从缓存表中判断是否已经生成数据
		if (!isMulti) {
			RptCase rptCase = rptCaseService.selectRptComDetailCase(month, latnId, type);
			if (rptCase != null) {
				ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCase.getRptCaseId());
				String path = cache.getFilePath();

				// 首先从文件主机下载文件
				FileService.pull(path);
				return path;
			}
		}

		List<Organization> orgs = localNetService.listUnderKids(latnId, isMulti);
		if (orgs == null || orgs.size() == 0) {
			throw new ReportException("选择营业区错误,请重试");
		}

		// 客户群
		List<Map<String, String>> cols =rptQueryComDetailDAO.selectIndexCodeAndName("1801");
		// 指标
		List<Map<String, String>> fields = rptEditionService.listComDetailRowMap();

		// 数据
		LinkedHashMap reportData = new LinkedHashMap<>();
		for (Organization org : orgs) {
			String name = org.getName();
			String id = org.getData();
			
			Map<String, Map<String, String>> data = getDataMap(month, id);
			reportData.put(name, data);
			
		}

		String latnName = orgs.get(0).getName();

		String templatePath = configTemplatePath();
		String outPath = configExportPath(month, latnName, type, isMulti);

		ReportExportServ export = new RptQueryComDetailExport();
		export.template(templatePath).out(outPath).column(cols).row(fields).data(reportData).export();

		return outPath;
	}

	/**
	 * 配置导出路径(包括文件名)
	 *
	 * @param month
	 * @param latnName
	 * @param type
	 * @param isMulti
	 * @return
	 * @throws IOException
	 */
	public String configExportPath(String month, String latnName, String type, Boolean isMulti) throws IOException {

		String sep = "_";
		String suffix;
		type = ReportConfig.getTaxType(type);

		if (isMulti) {
			suffix = "多营业区.xls";
		} else {
			suffix = "报表.xls";
		}

		String fileName = new StringBuffer("通信主业明细报表").append(sep).append(latnName).append(sep).append(month)
				.append(sep).append(type).append(sep).append(suffix).toString();
		fileName.replace("*", "");
		Path path = Paths.get(reportDownloadService.configLocation(), month);
		if (!Files.exists(path)) {
			Files.createDirectory(path);
		}
		return path.resolve(fileName).toString();
	}

	/**
	 * 配置模板路径 TODO 待整合(使用框架封装)
	 *
	 * @return
	 */
	public String configTemplatePath() {
		String sep = File.separator;
		String templateName = dictionaryService.getKidDataByName(ReportConfig.RptExportType.PARENT.toString(),
				ReportConfig.RptExportType.RPT_QUERY_COM_DETAIL.toString());
		return new StringBuffer(reportDownloadService.configTemplateLocation()).append(sep).append(templateName)
				.toString();
	}

	public void remove(String month, String latnId, String type) {
		RptCase rptCase = rptCaseService.selectRptComDetailCase(month, latnId, type);
		if (rptCase != null) {
			Long rptCaseId = rptCase.getRptCaseId();
			rptCaseService.removeCache(rptCaseId);
		}
	}

	public static class RptQueryComDetailExport extends ReportExportServ<Map<String, Map<String, String>>> {

		@Override
		protected List<String> processTitle(Sheet sheet, List<Map<String, String>> columns) throws Exception {

			int columnIndex = 3;// 列指针
			int rowIndex = 1;// 行指针
			Row nameRow = sheet.createRow(rowIndex);
			Row idRow = sheet.createRow(rowIndex + 1);
			List<String> ids = new ArrayList<>();
			
			CellStyle cellStyle = PoiUtils.setCellStyle(getWorkbook()); //居中
			for (int i = 0; i < columns.size(); i++) {
				int col = columnIndex *(i+1) ;
		
				Map<String, String> title = columns.get(i);
				String id = title.get("id");
				// 每隔3列合并行，写入客户群
				//第一行
				CellRangeAddress mergionCell = new CellRangeAddress(rowIndex, rowIndex, col, col + 2);
				sheet.addMergedRegion(mergionCell);
				Cell nameCell = nameRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String name = title.get("name");
				nameCell.setCellValue(name);
				nameCell.setCellStyle(cellStyle);
				
				//第二行
				Cell idCell = idRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				idCell.setCellValue("上年同期数");
				idCell.setCellStyle(cellStyle);
				ids.add("1_"+id);
				
				Cell idCell2 = idRow.getCell(col+1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				idCell2.setCellValue("本月发生数");
				idCell2.setCellStyle(cellStyle);
				ids.add("2_"+id);
				
				Cell idCell3 = idRow.getCell(col+2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				idCell3.setCellValue("本年累计数");
				idCell3.setCellStyle(cellStyle);
				ids.add("3_"+id);
				
			}

			return ids;
		}

		@Override
		protected void processSheet(Sheet sheet, List<String> columns, List<Map<String, String>> rows,
				Map<String, Map<String, String>> datas) throws Exception {
			int rowIndex = 3;// 行指针
			int columnIndex = 3;// 列指针
			String _append = "_";
			CellStyle cellStyle = PoiUtils.valueCellStyle(getWorkbook());

			for (int i = 0; i < rows.size(); i++) {
				Map<String, String> rowData = rows.get(i);

				String id = rowData.get("id");
				String code = rowData.get("code");
				String name = rowData.get("name");

				Row row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue(code);
				row.createCell(1).setCellValue(name);
				row.createCell(2).setCellValue(id);

				for (int j = 0; j < columns.size(); j++) {
					int tempIndex = columnIndex + j;					
					String key = id + _append + columns.get(j);
					Map<String, String> temp = datas.get(key);
					double data = temp == null ? 0 : Double.parseDouble(temp.get("data"));
					Cell cell = row.createCell(tempIndex);
					cell.setCellValue(data);
					cell.setCellStyle(cellStyle);
				}
				
			}

		}
	}

}
