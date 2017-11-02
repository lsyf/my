package com.loushuiyifan.report.serv;

import com.google.common.collect.Lists;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.CommonVO;
import com.loushuiyifan.system.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class DateService {
    public static final DateTimeFormatter YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter DDHH = DateTimeFormatter.ofPattern("ddHH");
    
    @Autowired
    DictionaryService dictionaryService;


    /**
     * 周围几个月(例如本月+前两月+后两月)
     *
     * @param num
     * @return
     */
    public List<CommonVO> aroundMonths(Integer num) {
        List<CommonVO> list = Lists.newArrayList();
        for (int i = num; i >= 0 - num; i--) {
            String date = LocalDate.now().plusMonths(i).format(YYYYMM);
            list.add(new CommonVO(date, date));
        }
        return list;
    }

    /**
     * 前几个月(例如本月+前两月)
     *
     * @param num
     * @return
     */
    public List<CommonVO> lastMonths(Integer num) {
        List<CommonVO> list = Lists.newArrayList();
        for (int i = 0; i >= 0 - num; i--) {
            String date = LocalDate.now().plusMonths(i).format(YYYYMM);
            list.add(new CommonVO(date, date));
        }
        return list;
    }

    /**
     * 校验 能否导入收入报账数据
     *
     * @param month
     */
    public void checkImportIncomeData(String month) {

        //首先当前导入时间校验
    	//TODO 之前为0324，测试暂时更改
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_INCOME_DATA.toString());
        String now = LocalDateTime.now().format(DDHH);
        if (now.compareTo(limitTime) > 0) {
            throw new ReportException("超出当前导入时间限制");
        }

        //然后账期校验
        //day为-1及负数代表任何账期都可以导入
        //day为0代表任何账期都不可以导入
        //day为其他正数代表: 当前时间大于则导入本月，否则为上月
        String day = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.MONTH_IMPORT_INCOME_DATA.toString());
        int limitDay = Integer.parseInt(day);
        int nowDay = LocalDate.now().getDayOfMonth();
        int value = nowDay > limitDay ? 0 : -1;
        if (limitDay == 0 || limitDay > 0
                && !LocalDate.now().plusMonths(value).format(YYYYMM).equals(month)) {
            throw new ReportException("超出导入账期限制");
        }
    }

    /**
     * 校验 能否导入C5数据
     *
     * @param month
     */
    public void checkImportC5(String month) {

        //首先当前导入时间校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_C5.toString());
        String now = LocalDateTime.now().format(DDHH);
        if (now.compareTo(limitTime) > 0) {
            throw new ReportException("超出当前导入时间限制");
        }

        //然后账期校验
        //day为-1及负数代表任何账期都可以导入
        //day为0代表任何账期都不可以导入
        //day为其他正数代表: 当前时间大于则导入本月，否则为上月
        String day = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.MONTH_IMPORT_C5.toString());
        int limitDay = Integer.parseInt(day);
        int nowDay = LocalDate.now().getDayOfMonth();
        int value = nowDay > limitDay ? 0 : -1;
        if (limitDay == 0 || limitDay > 0
                && !LocalDate.now().plusMonths(value).format(YYYYMM).equals(month)) {
            throw new ReportException("超出导入账期限制");
        }
    }

    /**
     * 校验 能否导入切割比例数据
     *
     * @param month
     */
    public void checkImportCut(String month) {

        //首先当前导入时间校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_TAX.toString());
        String now = LocalDateTime.now().format(DDHH);
        if (now.compareTo(limitTime) > 0) {
            throw new ReportException("超出当前导入时间限制");
        }

        //历史账期不能上传
        //day为-1及负数代表任何账期都可以导入
        //day为0代表任何账期都不可以导入
        //day为其他正数代表: 当前时间大于则导入本月，否则为上月
        String day = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.MONTH_IMPORT_TAX.toString());
        int limitDay = Integer.parseInt(day);
        int nowDay = LocalDate.now().getDayOfMonth();
        int value = nowDay > limitDay ? 0 : -1;
        if (limitDay == 0 || limitDay > 0
                && !LocalDate.now().plusMonths(value).format(YYYYMM).equals(month)) {
            throw new ReportException("超出导入账期限制");
        }
    }

    /**
     * 校验 能否导入指标组数据
     */
    public void checkImportGroup() {

    	//TODO 之前为多少，测试暂时改为多少
        //首先当前导入时间校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_GROUP.toString());
        String now = LocalDateTime.now().format(DDHH);
        if (now.compareTo(limitTime) > 0) {
            throw new ReportException("超出当前导入时间限制");
        }


    }

    /**
     * 校验 能否税务导入数据
     *
     * @param month
     */
    public void checkImportTax(String month) {

        //首先当前导入时间校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_TAX.toString());
        String now = LocalDateTime.now().format(DDHH);
        if (now.compareTo(limitTime) > 0) {
            throw new ReportException("超出当前导入时间限制");
        }

        //然后账期校验
        //day为-1及负数代表任何账期都可以导入
        //day为0代表任何账期都不可以导入
        //day为其他正数代表: 当前时间大于则导入本月，否则为上月
        String day = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.MONTH_IMPORT_TAX.toString());
        int limitDay = Integer.parseInt(day);
        int nowDay = LocalDate.now().getDayOfMonth();
        int value = nowDay > limitDay ? 0 : -1;
        if (limitDay == 0 || limitDay > 0
                && !LocalDate.now().plusMonths(value).format(YYYYMM).equals(month)) {
            throw new ReportException("超出导入账期限制");
        }
    }

    /**
     * 校验 能否导入业财差异数据
     *
     * @param month
     */
    public void checkImportYccy() {

        //首先当前导入校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_YCCY.toString());
        
        if (!"Y".equals(limitTime)) {
            throw new ReportException("超出当前导入限制");
        }

    }
    /**
     * 校验 能否删除业财差异数据
     *
     * @param month
     */
    public void checkDelYccy(String month) {
    	//首先当前导入校验
        String limitTime = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.TIME_IMPORT_YCCY.toString());
        
        if (!"Y".equals(limitTime)) {
            throw new ReportException("超出当前导入限制");
        }
        
    
        //然后账期校验
        //day为-1及负数代表任何账期都可以导入
        //day为0代表任何账期都不可以导入
        //day为其他正数代表: 当前时间大于则导入本月，否则为上月
        String day = dictionaryService.getKidDataByName(
                ReportConfig.RptAppParam.ROOT.toString(),
                ReportConfig.RptAppParam.MONTH_IMPORT_YCCY.toString());
       
        int limitDay = Integer.parseInt(day);
        int nowDay = LocalDate.now().getDayOfMonth();
        int value = nowDay > limitDay ? 0 : -1;
        
        if (limitDay == 0 || limitDay > 0 
                && !LocalDate.now().plusMonths(value).format(YYYYMM).equals(month)) {
            throw new ReportException("超出导入账期限制");
        }

    }
}
