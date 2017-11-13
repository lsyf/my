package com.loushuiyifan.report.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.dao.RptCustDefChannelDAO;
import com.loushuiyifan.report.dao.RptQueryDAO;
import com.loushuiyifan.report.dao.RptRepfieldDefChannelDAO;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.serv.ReportExportServ;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //客户群
        List<Map<String, String>> custs = rptCustDefChannelDAO.listMap("1701");
        //指标
        List<Map<String, String>> fields = rptRepfieldDefChannelDAO.listMap("1701");

        //数据
        List<Organization> orgs = localNetService.listUnderKids(latnId, isMulti);
        LinkedHashMap reportData = new LinkedHashMap<>();
        for (Organization org : orgs) {
            String name = org.getName();
            String id = org.getData();
            Map<String, Map<String, String>> data = rptQueryDAO.listAsMap(month, incomeSource, id, type);
            reportData.put(name, data);
        }

        String template = reportDownloadService.configTemplateLocation() + "/CwRptExcelChannel2017.xls";
        String out = "d:/a.xls";


        ReportExportServ export = new RptQueryExport();
        export.template(template)
                .out(out)
                .column(custs)
                .row(fields)
                .data(reportData)
                .export();

        return out;
    }


    @Autowired
    public ReportDownloadService reportDownloadService;

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
