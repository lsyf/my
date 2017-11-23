package com.loushuiyifan.report.service;

import com.loushuiyifan.report.dao.IncomeStatisticsDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 漏水亦凡
 * @date 2017/11/2
 */
@Service
public class IncomeStatisticsService {

    @Autowired
    IncomeStatisticsDAO incomeStatisticsDAO;

    public void allSum(String month, String latnId, Long userId) {
        SPDataDTO dto = new SPDataDTO();
        dto.setMonth(month);
        dto.setLatnId(latnId);
        dto.setUserId(userId);
        incomeStatisticsDAO.allSum(dto);
        if (dto.getRtnCode() != 0) {
            throw new ReportException(dto.getRtnMsg());
        }
    }
}
