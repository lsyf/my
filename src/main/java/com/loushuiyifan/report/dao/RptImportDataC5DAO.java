package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataC5;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.vo.ImportC5DataVO;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface RptImportDataC5DAO extends MyMapper<RptImportDataC5> {
    /**
     * c5导入-校验新存过
     * update_tax_c5_2015(原来的) -->update_tax_c5(新)
     *
     * @param dto
     */
    void checkC5Data(SPDataDTO dto);

    /**
     * c5导入-删除存过
     * IRPT_DEL_DATA_FOR_MGMT_C5
     *
     * @param dto
     */
    void deleteImportData(SPDataDTO dto);

    /**
     * c5导入查询-稽核汇总
     *
     * @return
     */
    List<ImportDataLogVO> jiheSum(@Param("month") String month,
                                  @Param("latnId") Integer latnId,
                                  @Param("type") String type
    );

    /**
     * c5导入查询-区域统计
     *
     * @return
     */
    List<ImportC5DataVO> areaCount(@Param("month") String month,
                                   @Param("latnId") Integer latnId);
}
