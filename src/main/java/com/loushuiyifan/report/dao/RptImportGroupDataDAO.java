package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataGroup;
import com.loushuiyifan.report.vo.ImportDataGroupVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportGroupDataDAO extends MyMapper<RptImportDataGroup> {

    void deleteGroup(@Param("latnid") Integer latnId,
                     @Param("groupid") Long groupId);

    List<String> findSubcode(String subcode);

    List<ImportDataGroupVO> listData(@Param("latnId") Integer latnId,
                                     @Param("groupId") Long groupId
    );


}
