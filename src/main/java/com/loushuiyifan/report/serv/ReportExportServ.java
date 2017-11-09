package com.loushuiyifan.report.serv;

import com.loushuiyifan.config.poi.AbstractPoiExport;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表导出
 *
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public abstract class ReportExportServ<E> extends AbstractPoiExport<E> {

    @Autowired
    public ReportDownloadService reportDownloadService;

    protected List<Map<String, String>> columns;
    protected LinkedHashMap<String, List<E>> datas;

    public ReportExportServ column(List<Map<String, String>> columns) {
        this.columns = columns;
        return this;
    }

    public ReportExportServ data(LinkedHashMap<String, List<E>> datas) {
        this.datas = datas;
        return this;
    }


    @Override
    protected void process(Workbook wb) throws Exception {
        assert hasTemp;

        //首先添加列名 并复制数据
        int index = 0;
        for (String key : datas.keySet()) {
            if (index == 0) {
                wb.setSheetName(0, key);
                Sheet sheet = wb.getSheetAt(0);
                processTitle(sheet, columns);
            } else {
                wb.cloneSheet(0);
                wb.setSheetName(index, key);
            }
            index++;
        }

        //然后填充数据
        for (Map.Entry<String, List<E>> e : datas.entrySet()) {
            String name = e.getKey();
            List<E> data = e.getValue();
            Sheet sheet = wb.getSheet(name);
            processSheet(sheet, data);

        }

    }

    abstract protected void processTitle(Sheet sheet, List<Map<String, String>> titles) throws Exception;

    abstract protected void processSheet(Sheet sheet, List<E> data) throws Exception;

}
