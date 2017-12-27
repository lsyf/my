package com.loushuiyifan.report.serv;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.loushuiyifan.config.poi.AbstractPoiExport;
import com.loushuiyifan.report.exception.ReportException;

/**
 * 报表导出
 *
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public class CommonExportServ extends AbstractPoiExport<Map<String, String>> {

    //数据(key->name)
    protected List<Map<String, String>> reportDatas;

    //列值
    protected String[] reportKeys;
    //列名
    protected String[] reportTitles;


    public CommonExportServ column(String[] keys, String[] titles) {
        if (keys.length != titles.length) {
            throw new ReportException("导出文件列数据配置错误!");
        }
        this.reportKeys = keys;
        this.reportTitles = titles;
        return this;
    }


    public CommonExportServ data(List<Map<String, String>> datas) {
        this.reportDatas = datas;
        return this;
    }


    public byte[] exportData() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        out(os);
        super.export();
        return os.toByteArray();
    }
    

    @Override
    protected void process(Workbook wb) throws Exception {

        Sheet sheet = wb.createSheet("数据");

        //首先添加表头
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < reportTitles.length; i++) {
            titleRow.createCell(i).setCellValue(reportTitles[i]);
        }

        //然后填充数据
        for (int i = 0; i < reportDatas.size(); i++) {
            int rowIndex = i + 1;
            Row row = sheet.createRow(rowIndex);

            Map<String, String> map = reportDatas.get(i);
            for (int j = 0; j < reportKeys.length; j++) {
                String key = reportKeys[j];

                Object obj =map.get(key);
                String data = obj ==null?"-":obj.toString();
                row.createCell(j).setCellValue(data);
            }
        }

    }
}
