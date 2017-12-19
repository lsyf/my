package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.dao.RptTaxQueryDAO;
import com.loushuiyifan.report.serv.TaxExportServ;
import com.loushuiyifan.report.vo.RptQueryDataVO;

@Service
public class RptTaxQueryService {
    private static final Logger logger = LoggerFactory.getLogger(RptTaxQueryService.class);

    @Autowired
    RptTaxQueryDAO rptTaxQueryDAO;


    public List<Map<String, String>> listAreaInfo() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> m = Maps.newHashMap();
        m.put("id", "0");
        m.put("name", "全部");
        m.put("data", "0");
        m.put("lvl", "1");
        list.add(m);

        List<Map<String, String>> list2 = rptTaxQueryDAO.listAreaForTax();
        list.addAll(list2);
        return list;
    }

    public RptQueryDataVO list(String month, String latnId, String taxType) {

        //先查询表头
        List<Map<String, String>> cols = getCols(latnId, taxType);
        //查询行信息
        List<Map<String, String>> rows = getRows(latnId, taxType);
        //查询具体数据
        Map<String, Map<String, String>> datas = getDatas(month, latnId, taxType);

        //生成html需求数据模型
        //首先遍历指标,建立 id->feild
        Map<String, Map<String, String>> rowMap = Maps.newHashMapWithExpectedSize(2000);
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            rowMap.put(row.get("id"), row);
        }

        //填充行数据
        //遍历数据，分别插入到指标数据中
        Iterator<String> it = datas.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String[] xy = key.replace("key_", "").split("_", 2);
            String x = xy[0];
            String y = xy[1];
            String v = datas.get(key).get("data");

            Map<String, String> row = rowMap.get(x);
            if (row == null) {
                continue;
            }
            row.put(y, v);
        }

        RptQueryDataVO vo = new RptQueryDataVO();
        vo.setTitles(cols);
        vo.setDatas(rows);

        return vo;
    }

    private Map<String, Map<String, String>> getDatas(String month, String latnId, String taxType) {
        Map<String, Map<String, String>> datas;
        switch (taxType) {
            case "1":
                datas = rptTaxQueryDAO.getData1(month);
                break;
            case "2":
                datas = rptTaxQueryDAO.getData2(month);
                break;
            case "3":
                datas = rptTaxQueryDAO.getData3(month);
                break;
            case "4":
                datas = rptTaxQueryDAO.getData4(month, latnId);
                break;
            case "5":
                datas = rptTaxQueryDAO.getData5(month);
                break;
            case "6":
                datas = rptTaxQueryDAO.getData6(month);
                break;
            case "7":
                datas = rptTaxQueryDAO.getData7(month);
                break;
            case "8":
                datas = rptTaxQueryDAO.getData8(month);
                break;
            default:
                datas = Maps.newHashMap();
        }
        return datas;
    }

    private List<Map<String, String>> getRows(String latnId, String taxType) {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> map;
        switch (taxType) {
            case "1":
            case "3":
                list = rptTaxQueryDAO.getRow1(latnId);
                break;
            case "2":
                list = rptTaxQueryDAO.getRow2(latnId);
                break;

            case "4":
                String[] names = {"营业收入（帐载收入）", "营业外收入（帐载收入", "营业收入（调整会计收入）", "营业外收入（调整会计收入）"};
                for (int i = 1; i <= 4; i++) {
                    map = new HashMap<>();
                    map.put("id", i + "");
                    map.put("type", "增值税");
                    map.put("name", names[i - 1]);
                    list.add(map);
                }
                break;
            case "5":
            case "6":
                list = rptTaxQueryDAO.getRow5(latnId);
                break;
            case "7":
                list = rptTaxQueryDAO.getRow7();
                break;
            case "8":
                list = rptTaxQueryDAO.getRow8(latnId);
                break;

            default:
        }
        return list;
    }

    private List<Map<String, String>> getCols(String latnId, String taxType) {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> map;
        String[] ids;
        String[] names;
        switch (taxType) {
            case "1":
                ids = new String[]{"profitCode", "department",
                        "id", "taxName", "taxProfitCode", "taxProfitName"};
                names = new String[]{"利润中心组编号", "单位名称",
                        "纳税识别号", "纳税组织名称", "税金利润中心编码", "税金利润中心名称"};
                list = generateCol(ids, names);
                list.addAll(rptTaxQueryDAO.getColumn1());
                break;
            case "2":
                ids = new String[]{"name", "id", "1000", "222109"};
                names = new String[]{"利润中心", "纳税识别号", "营业收入（经营收入", "收入平台-销项税（所有的税）"};
                list = generateCol(ids, names);
                break;
            case "3":
                ids = new String[]{"profitCode", "department",
                        "id", "taxName", "taxProfitCode", "taxProfitName",
                        "1", "2"};
                names = new String[]{"利润中心组编号", "单位名称",
                        "纳税识别号", "纳税组织名称", "税金利润中心编码", "税金利润中心名称",
                        "经营收入", "增值税收入"};
                list = generateCol(ids, names);

                break;
            case "4":
                ids = new String[]{"type", "name", "1", "2", "3"};
                names = new String[]{"税种", "收入类别", "会计收入", "税务收入", "税额"};
                list = generateCol(ids, names);
                break;
            case "5":
                ids = new String[]{"profitCode", "department",
                        "id", "taxName", "taxProfitCode", "taxProfitName", "percent", "1"};
                names = new String[]{"利润中心组编号", "单位名称",
                        "纳税识别号", "纳税组织名称", "税金利润中心编码", "税金利润中心名称", "预收账款分摊比", "分摊金额"};
                list = generateCol(ids, names);
                break;
            case "6":
                ids = new String[]{"profitCode", "department",
                        "id", "taxName", "taxProfitCode", "taxProfitName", "percent",
                        "1", "2", "3", "4", "5", "6", "7", "8"};
                names = new String[]{"利润中心组编号", "单位名称",
                        "纳税识别号", "纳税组织名称", "税金利润中心编码", "税金利润中心名称", "预征率",
                        "增值税收入", "征税服务合计", "基础电信服务0101", "增值电信服务0102",
                        "综合其他应税服务", "销售货物", "预收账款", "预缴额"};
                list = generateCol(ids, names);
                break;
            case "7":
                ids = new String[]{"id", "name"};
                names = new String[]{"税务科目", "项目"};
                list = generateCol(ids, names);
                List<Map<String, String>> tempList = rptTaxQueryDAO.getColumn7(latnId);
                for (Map m : tempList) {
                    map = new HashMap<>();
                    map.put("id", m.get("id") + "_1");
                    map.put("name", m.get("name") + "_收入");
                    list.add(map);
                    map = new HashMap<>();
                    map.put("id", m.get("id") + "_2");
                    map.put("name", m.get("name") + "_销项税");
                    list.add(map);
                }
                break;
            case "8":
                ids = new String[]{"name", "id",
                        "1", "2", "3", "4", "5", "6"};
                names = new String[]{"单位", "税金利润中心编号",
                        "11%收入占比", "6%收入占比", "增值电信业务占比"
                        , "现代服务业务收入占比", "17%收入占比", "商品销售收入占比"};
                list = generateCol(ids, names);
                break;
            default:
        }

        return list;

    }

    private List<Map<String, String>> generateCol(String[] ids, String[] names) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("id", ids == null ? (i + 1 + "") : ids[i]);
            map.put("name", names[i]);
            list.add(map);
        }
        return list;
    }

    

    public byte[] export(String month, String latnId, String taxType) 
    		throws Exception{
       
    	//先查询表头
        List<Map<String, String>> cols = getCols(latnId, taxType);
        //查询行信息
        List<Map<String, String>> rows = getRows(latnId, taxType);
        //查询具体数据
        Map<String, Map<String, String>> datas = getDatas(month, latnId, taxType);

      //生成html需求数据模型
        //首先遍历指标,建立 id->feild
        Map<String, Map<String, String>> rowMap = Maps.newHashMapWithExpectedSize(2000);
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            rowMap.put(row.get("id"), row);
        }

        //填充行数据
        //遍历数据，分别插入到指标数据中
        Iterator<String> it = datas.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String[] xy = key.replace("key_", "").split("_", 2);
            String x = xy[0];
            String y = xy[1];
            String v = datas.get(key).get("data");

            Map<String, String> row = rowMap.get(x);
            if (row == null) {
                continue;
            }
            row.put(y, v);
        }
        
        byte[] data = new TaxExportServ().column(cols).row(rows).exportData();
    	
        return data;
    }
    
    
    public String getFileName(String month, String latnId, String taxType) {
    	String latnName ="全部";
    	if(latnId != "0"){
    		latnName =rptTaxQueryDAO.getLatnIdName(latnId);
        }
    	String tax =getTaxType(taxType);
    
        return latnName+"_"+tax+"_"+month+".xls";
    }
    
    public String getTaxType(String type){
    	String tax ="";
    	switch (type) {
		
		case "1":
			tax ="纳税组织按税目收入统计表";
			break;
		case "2":
			tax ="收入&销项税统计税";
			break;
		case "3":
			tax ="收入汇总表";
			break;
		case "4":
			tax ="应税收入汇总表";
			break;
		case "5":
			tax ="预收账款分摊";
			break;
		case "6":
			tax ="增值税预缴表";
			break;
		case "7":
			tax ="收入税目结构当月";
			break;
		case "8":
			tax ="收入税率结构分析";
			break;
		default:
		}
    	 return tax;
    }
    
    
}
