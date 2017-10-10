package com.loushuiyifan.data.service;


import com.loushuiyifan.common.util.DecimalUtils;
import com.loushuiyifan.data.dao.Report1DAO;
import com.loushuiyifan.data.dao.Report2DAO;
import com.loushuiyifan.data.dao.Report3DAO;
import com.loushuiyifan.data.vo.ProductConfig;
import com.loushuiyifan.data.vo.Report1;
import com.loushuiyifan.data.vo.Report2;
import com.loushuiyifan.data.vo.Report3;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/6/6
 */
@Service
public class TestService {

    @Autowired
    Report1DAO report1DAO;

    @Autowired
    Report2DAO report2DAO;

    @Autowired
    Report3DAO report3DAO;


    public List<Report1> getProvinceMFBg(String start, String end) {
        List<Report1> list = report1DAO.listProvinceMFBg(start, end);
        Map<String, BigDecimal> map = getBudget(start, end);


        for (Report1 r : list) {
            String month = r.getMonth();
            //保留两位小数
            r.setMobile(Double.parseDouble(String.format("%.2f", r.getMobile())));
            r.setFixNetwork(Double.parseDouble(String.format("%.2f", r.getFixNetwork())));

            double all = r.getMobile() + r.getFixNetwork();

            BigDecimal budget = map.get(month);

            //如果预算值不为空
            if (budget != null) {
                double value = budget.doubleValue();
                r.setBudgetGap(Double.parseDouble(String.format("%.2f", (all - value))));
            }
        }
        return list;
    }

    /**
     * 获取 预算数据
     *
     * @param start
     * @param end
     * @return
     */
    public Map<String, BigDecimal> getBudget(String start, String end) {
        List<Map<String, Object>> list = report1DAO.listBudget(start, end);
        Map<String, BigDecimal> map = new HashMap<>();
        for (Map<String, Object> temp : list) {
            String month = (String) temp.get("month");
            BigDecimal budget = (BigDecimal) temp.get("budget");
            map.put(month, budget);
        }
        return map;
    }


    /**
     * 获取分产品层级数据
     *
     * @param month
     * @return
     */
    public Report2 getProductLevel(String month) {

        Report2 report = new Report2();
        report.setMonth(month);

        //先查询移动 固网汇总数据
        Map<String, BigDecimal> sumMF = report2DAO.getSumMF(month);
        if (sumMF == null) {
            return new Report2();
        }
        report.setMobile(DecimalUtils.formatDecimal(sumMF.get("mobile"), 2));
        report.setFixNetwork(DecimalUtils.formatDecimal(sumMF.get("fix_network"), 2));

        //查询其子产品配置
        List<ProductConfig> configs = report2DAO.listProductConfig();

        //分别查询子产品数据
        Map<String, BigDecimal> datas1 = report2DAO.listProductData(1, configs, month);
        Map<String, BigDecimal> datas2 = report2DAO.listProductData(2, configs, month);

        List<Map<String, Object>> mobileProduct = new ArrayList<>();
        List<Map<String, Object>> fixNetworkProduct = new ArrayList<>();
        for (ProductConfig config : configs) {
            String name = config.getName();

            //移动
            Map<String, Object> map1 = new HashMap<>();
            double data1 = DecimalUtils.formatDecimal(datas1.get(name), 2);
            map1.put("name", "移动-" + name);
            map1.put("data", data1);
            mobileProduct.add(map1);

            //固网
            Map<String, Object> map2 = new HashMap<>();
            double data2 = DecimalUtils.formatDecimal(datas2.get(name), 2);
            map2.put("name", "固网-" + name);
            map2.put("data", data2);
            fixNetworkProduct.add(map2);
        }

        report.setMobileProduct(mobileProduct);
        report.setFixNetworkProduct(fixNetworkProduct);

        return report;
    }

    @Cacheable("report3")
    public List<Report3> getReport3(String month) {

        //处理日期参数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate date = LocalDate.parse(month + "01", DateTimeFormatter.BASIC_ISO_DATE);
        String thisMonth = month;
        String lastMonth = formatter.format(date.plusMonths(-1));
        String firstMonth = formatter.format(date.with(TemporalAdjusters.firstDayOfYear()));
        String firstMonthLastYear = formatter.format(date.plusYears(-1).with(TemporalAdjusters.firstDayOfYear()));
        String thisMonthLastYear = formatter.format(date.plusYears(-1));


        //首先获取 产品配置
        List<ProductConfig> configs = report3DAO.listProductConfig();

        List<Report3> list = new ArrayList<>();

        BigDecimal total = null;
        //根据配置 获取各类数据
        for (ProductConfig config : configs) {

            Report3 report3 = new Report3();
            report3.setName(config.getName());

            String condition = config.getConditions();
            if (StringUtils.isEmpty(config.getConditions())) {
                list.add(report3);
                continue;
            }

            BigDecimal thisMonthTotal = report3DAO.getMonthTotal(condition, thisMonth);
            BigDecimal lastMonthTotal = report3DAO.getMonthTotal(condition, lastMonth);

            BigDecimal thisYearTotal = report3DAO.getYearTotal(condition, firstMonth, thisMonth);
            BigDecimal lastYearTotal = report3DAO.getYearTotal(condition, firstMonthLastYear, thisMonthLastYear);

            if (thisMonthTotal != null) {
                //本月实绩
                report3.setMonthTotal(DecimalUtils.formatDecimal(thisMonthTotal, 2));

                if (lastMonthTotal != null) {
                    BigDecimal temp = thisMonthTotal.subtract(lastMonthTotal);
                    //环比差额
                    report3.setMonthDiff(DecimalUtils.formatDecimal(temp, 2));
                }
            }

            if (thisYearTotal != null) {
                //本年累计
                report3.setYearTotal(DecimalUtils.formatDecimal(thisYearTotal, 2));

                if (lastYearTotal != null) {
                    BigDecimal temp = thisYearTotal.subtract(lastYearTotal).divide(thisYearTotal, 4, BigDecimal.ROUND_HALF_EVEN);
                    //累计同比增长率
                    report3.setYearGrowRate(DecimalUtils.formatDecimal(temp, 4));
                }
            }

            //如果是 主营业务收入
            if ("主营业务收入".equals(config.getName())) {
                total = thisMonthTotal;

                //预算缺口
                BigDecimal monthIsee = report3DAO.getMonthIsee(thisMonth);
                if (monthIsee != null) {
                    BigDecimal temp = thisMonthTotal.subtract(monthIsee);
                    report3.setMonthGap(DecimalUtils.formatDecimal(temp, 2));
                }

                BigDecimal yearIsee = report3DAO.getYearIsee(firstMonth, thisMonth);
                if (yearIsee != null && thisYearTotal != null) {
                    //累计预算缺口
                    BigDecimal yearGap = thisYearTotal.subtract(yearIsee);
                    report3.setYearGap(DecimalUtils.formatDecimal(yearGap, 2));

                    //年度预算完成率
                    BigDecimal yearComRate = thisYearTotal.divide(yearIsee, 4, BigDecimal.ROUND_HALF_EVEN);
                    report3.setYearComRate(DecimalUtils.formatDecimal(yearComRate, 4));

                }

            } else {
                if (total != null && thisMonthTotal != null) {
                    //占总收入占比
                    BigDecimal percent = thisMonthTotal.divide(total, 4, BigDecimal.ROUND_HALF_EVEN);
                    report3.setPercent(DecimalUtils.formatDecimal(percent, 4));
                }
            }

            list.add(report3);
        }


        return list;
    }


}
