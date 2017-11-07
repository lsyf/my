package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportCutRate;
import com.loushuiyifan.report.vo.CutDataListVO;
import com.loushuiyifan.report.vo.CutRateVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutRateDAO extends MyMapper<RptImportCutRate> {
    //查询比例是否存在
    Double calcRateSum(@Param("ruleId") String ruleId,
                       @Param("month") String month);

    //统计切割比例之和
    List<CutRateVO> sumRateByRuleId(@Param("ruleId") String ruleId,
                                    @Param("month") String month);

    //查询导入数据
    List<CutDataListVO> cutRateList(@Param("month") String month,
                                    @Param("latnId") Integer latnId,
                                    @Param("incomeSource") String incomeSource,
                                    @Param("shareType") Integer shareType,
                                    @Param("remark") String remark
    );

    //删除导入数据
    void cutRateDel(@Param("latnId") Integer latnId,
                    @Param("incomeSource") String incomeSource,
                    @Param("shareType") Integer shareType,
                    @Param("username") String username
    );

}
