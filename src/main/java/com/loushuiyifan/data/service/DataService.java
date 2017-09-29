package com.loushuiyifan.data.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.data.dao.DataDAO;
import com.loushuiyifan.data.vo.DataAnalysis;
import com.loushuiyifan.data.vo.MonthData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
@Service
public class DataService {

    @Autowired
    DataDAO dataDAO;

    public List<Map<String, Object>> da1() {
        List<MonthData> datas = dataDAO.da1();

        //转换格式
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> lastYear = Maps.newHashMap();
        Map<String, Object> thisYear = Maps.newHashMap();
        list.add(lastYear);
        list.add(thisYear);

        double lastTotal = 0,
                thisTotal = 0,
                lastPeriod = 0;


        int index = 0;//年份下标
        int m = 1;//月份
        for (MonthData d : datas) {
            String month = d.getAcctMonth();
            double amount = d.getAftAmount();

            Map<String, Object> data = list.get(index);

            //首月添加年份标记
            if (m == 1) {
                String year = month.substring(0, 4);
                data.put("year", year);
            }

            if (index == 0) {
                lastTotal += amount;
            } else if (index == 1) {
                thisTotal += amount;
            }

            //按月份加入数据
            data.put(m + "", amount);

            m++;
            if (m > 12) {
                m = 1;
                index++;
            }
        }
        //判断数据当前月份
        m = m == 1 ? 12 : m - 1;

        for (; m > 0; m--) {
            double d = (double) lastYear.get("" + m);
            lastPeriod += d;
        }

        lastYear.put("total", lastTotal);
        lastYear.put("period", lastPeriod);
        thisYear.put("period", thisTotal);
        thisYear.put("total", thisTotal);

        return list;
    }


    public List<DataAnalysis> da2(String month) {
        return dataDAO.da2(month);
    }
}
