package com.loushuiyifan.report.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataGroup;
import com.loushuiyifan.report.vo.ImportDataGroupVO;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportGroupDataDAO extends MyMapper<RptImportDataGroup> {

    void deleteGroup(@Param("latnid")Integer latnId, 
    		         @Param("groupid")Long groupId);

    List<String> findSubcode(Long subcode);
   
    List<ImportDataGroupVO> listData(@Param("latnId") Integer latnId,
							         @Param("groupId") Long groupId
							         );


}
