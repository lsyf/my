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
    //删除
    void deleteGroup(@Param("latnId") Integer latnId,
                     @Param("groupId") String groupId);

    //判断指标编码
    List<String> findSubcode(@Param("subcode") String subcode,
                             @Param("rptNo") String rptNo,
                             @Param("groupId") Long groupId);

    //查询数据
    List<ImportDataGroupVO> listData(@Param("latnId") Integer latnId,
                                     @Param("groupId") String groupId
    );


}
