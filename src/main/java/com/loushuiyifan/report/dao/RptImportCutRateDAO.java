package com.loushuiyifan.report.dao;

import java.util.List;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportCutRate;
import com.loushuiyifan.report.vo.CutDataListVO;
import com.loushuiyifan.report.vo.CutRateVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutRateDAO extends MyMapper<RptImportCutRate> {
    Double calcRateSum(@Param("ruleId") String ruleId,
                       @Param("month") String month);
    //统计切割比例之和
    List<CutRateVO> sumRateByRuleId(@Param("ruleId")String ruleId, 
    		                        @Param("month")String month);
    //查询导入数据 
    List<CutDataListVO> cutRateList(@Param("month")String month,
    		@Param("latnId") Integer latnId,
    		@Param("incomeSource")String incomeSource,
    		@Param("shareType")Integer shareType,
    		@Param("type")String type);
    
    //删除导入数据
    void cutRateDel(@Param("latnId") Integer latnId,
    		        @Param("incomeSource") String incomeSource,
                    @Param("shareType") Integer shareType,
                    @Param("userName") String userName,
                    @Param("activeFlag") String activeFlag);

}
