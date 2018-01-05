package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptSettleAmountDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CommonExportServ;
import com.loushuiyifan.report.vo.SettleAmountDataVO;

@Service
public class RptSettleAmountService {
    private static final Logger logger = LoggerFactory.getLogger(RptSettleAmountService.class);
    @Autowired
    RptSettleAmountDAO rptSettleAmountDAO;

    /**
     * 查询
     */
    public List<SettleAmountDataVO> listSettle(String month, String latnId, String zbCode) {

        List<SettleAmountDataVO> list = rptSettleAmountDAO.listData(month, latnId, zbCode);
        if (list == null ||list.size()==0) {
            throw new ReportException("查询数据为空！");
        }
        return list;
    }

    /**
     * 汇总
     */
    public String collect(String month) {
           	
		SPDataDTO dto = new SPDataDTO();
        dto.setMonth(month);
        rptSettleAmountDAO.collectData(dto);
        String code = dto.getRtnMsg2();
        if (!"0".equals(code)) {//非0为失败
            throw new ReportException("数据汇总失败: " + dto.getRtnMsg());
        }
    
        return dto.getRtnMsg();
    }

    /**
     * 导出数据
     */
    public byte[] export(String month, String latnId, String zbCode) throws Exception {

        List<Map<String, String>> list = rptSettleAmountDAO.listDataForMap(month, latnId, zbCode);

        String[] keys = {"val", "val1", "val2", "val3", "val4", "val5", "val6", "val7", "val8", "val9",
                "val10", "val11", "val12", "val13", "val14", "val15", "val16", "val17", "val18", "val19",
                "val20", "val21", "val22", "val23", "val24", "val25", "val26", "val27", "val28", "val29"};


        String[] str = rptSettleAmountDAO.listCodeName();
        String[] titles = new String[30];
        titles[0] = "地市";
        for (int i = 0; i < str.length; i++) {
            titles[i + 1] = str[i];
        }

        byte[] data = new CommonExportServ().column(keys, titles)
                .data(list).type("settle")
                .exportData();

        return data;
    }


}
