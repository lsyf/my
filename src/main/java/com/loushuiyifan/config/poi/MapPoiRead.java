package com.loushuiyifan.config.poi;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 16:26.
 */
public class MapPoiRead extends AbstractPoiRead<Map<String, String>> {
    private String[] keys = null;

    /**
     * 初始化
     *
     * @param keys 属性集合
     */
    public MapPoiRead(String[] keys) {
        this.keys = keys;
    }

    @Override
    protected List<Map<String, String>> process(Workbook wb) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<Map<String, String>> list = new ArrayList<>();
        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < keys.length; i++) {
                    map.put(keys[i], getCellData(row.getCell(i), evaluator));
                }
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 获取文件值
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> read() throws Exception {
        return super.read();
    }
}
