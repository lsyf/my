package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataICT;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataICTDAO extends MyMapper<RptImportDataICT> {

    /**
     * 校验数据
     * 存过: PRO_DATA_CHECK_ICTHIS
     *
     * @param dto
     */
    void checkImportData(SPDataDTO dto);

    /**
     * 删除数据
     * 存过: IRPT_DEL_ICTDATA
     *
     * @param dto
     */
    void deleteImportData(SPDataDTO dto);


    /**
     * 根据用户 和账期 查询ICT导入日志
     *
     * @param userId
     * @param month
     * @param type
     * @return
     */
    List<ImportDataLogVO> listICTLog(@Param("userId") Long userId,
                                     @Param("month") String month,
                                     @Param("type") String type);
}
