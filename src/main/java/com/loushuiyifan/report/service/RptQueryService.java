package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.serv.ReportExportServ;
import com.loushuiyifan.system.service.DictionaryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Service
public class RptQueryService {

    private static Logger logger = LoggerFactory.getLogger(RptQueryService.class);

    @Autowired
    RptCustDefChannelDAO rptCustDefChannelDAO;

    @Autowired
    RptRepfieldDefChannelDAO rptRepfieldDefChannelDAO;

    @Autowired
    RptQueryDAO rptQueryDAO;

    @Autowired
    LocalNetService localNetService;

    @Autowired
    CodeListTaxService codeListTaxService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    ReportDownloadService reportDownloadService;

    public Map<String, Object> list(String month,
                                    String latnId,
                                    String incomeSource,
                                    String type) {

        //客户群
        List<Map<String, String>> custs = rptCustDefChannelDAO.listMap("1701");
        //指标
        List<Map<String, String>> fields = rptRepfieldDefChannelDAO.listMap("1701");
        //数据
        Map<String, Map<String, String>> datas = rptQueryDAO.listAsMap(month, latnId, incomeSource, type);

        //首先遍历指标
        Map<String, Map<String, String>> fieldMap = Maps.newHashMapWithExpectedSize(2000);
        for (int i = 0; i < fields.size(); i++) {
            Map<String, String> temp = fields.get(i);
            fieldMap.put(temp.get("id"), temp);
        }

        //遍历数据，分别插入到指标数据中
        Iterator<String> it = datas.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String[] xy = key.split("_");
            String x = xy[0];
            String y = xy[1];
            String v = datas.get(key).get("data");

            Map<String, String> temp = fieldMap.get(x);
            if (temp == null) {
                continue;
            }
            temp.put(y, v);
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("titles", custs);
        result.put("datas", fields);
        return result;
    }

    public String export(String month,
                         String latnId,
                         String incomeSource,
                         String type,
                         Boolean isMulti) throws Exception {
        List<Organization> orgs = localNetService.listUnderKids(latnId, isMulti);
        if (orgs == null || orgs.size() == 0) {
            throw new ReportException("选择营业区错误,请重试");
        }

        //客户群
        List<Map<String, String>> custs = rptCustDefChannelDAO.listMap("1701");
        //指标
        List<Map<String, String>> fields = rptRepfieldDefChannelDAO.listMap("1701");

        //数据
        LinkedHashMap reportData = new LinkedHashMap<>();
        for (Organization org : orgs) {
            String name = org.getName();
            String id = org.getData();
            Map<String, Map<String, String>> data = rptQueryDAO.listAsMap(month, incomeSource, id, type);
            reportData.put(name, data);
        }

        String latnName = orgs.get(0).getName();
        String isName = codeListTaxService.
                getIncomeSource("income_source2017", incomeSource).getCodeName();


        String templatePath = configTemplatePath();
        String outPath = configExportPath(month,
                latnName, isName, type, isMulti);


        ReportExportServ export = new RptQueryExport();
        export.template(templatePath)
                .out(outPath)
                .column(custs)
                .row(fields)
                .data(reportData)
                .export();

        return outPath;
    }

    public String configExportPath(String month,
                                   String latnName,
                                   String isName,
                                   String type,
                                   Boolean isMulti) throws IOException {

        String sep = "_";
        String suffix;
        switch (type) {
            case "0":
                type = "价税合一(含税)";
                break;
            case "1":
                type = "增值税";
                break;
            case "2":
                type = "价(不含税)";
                break;
        }
        if (isMulti) {
            suffix = "多营业区.xls";
        } else {
            suffix = "报表.xls";
        }

        String fileName = new StringBuffer(latnName)
                .append(sep).append(month)
                .append(sep).append(isName)
                .append(sep).append(type)
                .append(sep).append(suffix)
                .toString();

        Path path = Paths.get(reportDownloadService.configLocation(), month);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.resolve(fileName).toString();
    }

    public String configTemplatePath() {
        String sep = File.separator;
        String templateName = dictionaryService.getKidDataByName(ReportConfig.RptExportType.PARENT.toString(),
                ReportConfig.RptExportType.RPT_QUERY.toString());
        return new StringBuffer(reportDownloadService.configTemplateLocation())
                .append(sep).append(templateName).toString();
    }


    public static class RptQueryExport extends ReportExportServ<Map<String, Map<String, String>>> {


        @Override
        protected List<String> processTitle(Sheet sheet, List<Map<String, String>> columns) throws Exception {

            int columnIndex = 4;//列指针
            int rowIndex = 3;//行指针
            Row idRow = sheet.createRow(rowIndex);
            Row nameRow = sheet.createRow(rowIndex + 1);
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                Map<String, String> title = columns.get(i);
                Cell idCell = idRow.getCell(columnIndex + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell nameCell = nameRow.getCell(columnIndex + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String id = title.get("id");
                String name = title.get("name");
                idCell.setCellValue(id);
                nameCell.setCellValue(name);

                ids.add(id);
            }

            return ids;
        }

        @Override
        protected void processSheet(Sheet sheet,
                                    List<String> columns,
                                    List<Map<String, String>> rows,
                                    Map<String, Map<String, String>> datas) throws Exception {
            int rowIndex = 5;//行指针
            int columnIndex = 4;//列指针

            String _append = "_";
            for (int i = 0; i < rows.size(); i++) {
                Map<String, String> rowData = rows.get(i);

                int rowNum = i + 1;
                String id = rowData.get("id");
                String pid = rowData.get("pid");
                String name = rowData.get("name");

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(id);
                row.createCell(2).setCellValue(pid);
                row.createCell(3).setCellValue(name);

                for (int j = 0; j < columns.size(); j++) {
                    int tempIndex = columnIndex + j;
                    String key = id + _append + columns.get(j);
                    Map<String, String> temp = datas.get(key);
                    double data = temp == null ? 0 : Double.parseDouble(temp.get("data"));
                    row.createCell(tempIndex).setCellValue(data);
                }

            }


        }
    }
}
