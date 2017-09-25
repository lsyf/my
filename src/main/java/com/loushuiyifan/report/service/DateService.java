package com.loushuiyifan.report.service;

import com.google.common.collect.Lists;
import com.loushuiyifan.report.vo.CommonVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class DateService {
    public static final DateTimeFormatter YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");

    public List<CommonVO> aroundMonths(Integer num) {
        List<CommonVO> list = Lists.newArrayList();
        for (int i = num; i >= 0 - num; i--) {
            String date = LocalDate.now().plusMonths(i).format(YYYYMM);
            list.add(new CommonVO(date, date));
        }
        return list;
    }

    public List<CommonVO> lastMonths(Integer num) {
        List<CommonVO> list = Lists.newArrayList();
        for (int i = 0; i >= 0 - num; i--) {
            String date = LocalDate.now().plusMonths(i).format(YYYYMM);
            list.add(new CommonVO(date, date));
        }
        return list;
    }
}
