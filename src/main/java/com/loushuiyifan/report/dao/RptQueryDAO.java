package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.CommonVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptQueryDAO extends MyMapper<CommonVO> {

    @MapKey("key")
    Map<String, Map<String, String>> listAsMap(@Param("month") String month,
                                               @Param("incomeSource") String incomeSource,
                                               @Param("latnId") String latnId,
                                               @Param("type") String type);


    String hasAuditAuthority(@Param("userId") Long userId,
                             @Param("rptCaseId") Long rptCaseId);

    void auditRpt(SPDataDTO dto);

    void selectAudits(Map map);
}
