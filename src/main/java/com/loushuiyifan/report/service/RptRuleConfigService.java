package com.loushuiyifan.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.controller.RptFundsFeeQueryController;
import com.loushuiyifan.report.controller.rest.BaseReportController;
import com.loushuiyifan.report.dao.RptImportCutDataDAO;
import com.loushuiyifan.report.dao.RptRuleConfigDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.RuleConfigVO;
import com.loushuiyifan.system.service.OrganizationService;

import oracle.net.aso.o;

@Service
public class RptRuleConfigService extends BaseReportController{
	private static final Logger logger = LoggerFactory.getLogger(RptRuleConfigService.class);
	@Autowired
	RptRuleConfigDAO rptRuleConfigDAO;
	
	/**
	 * 查询
	 * @param month
	 * @param latnId
	 * @return
	 */
	public List<RuleConfigVO> list(String month,String latnId, String cardType,
                                   String discount, Long logId){
		
		List<RuleConfigVO> list = rptRuleConfigDAO.listData(month, latnId, cardType, 
				                                            logId, discount);
		
		return list;
	}
	
	public void updateRule(String month,String cardType,String discount, 
                           String platformAmount,String inactiveAmount,Long logId){
	
		rptRuleConfigDAO.updateByAll(month,logId,cardType, discount, platformAmount, inactiveAmount);
	}
	
	public List<String> checkUsers(Long logId,Long userId){
		return rptRuleConfigDAO.checkUser(logId, userId);
	}
	/**
	 * 查询卡的类型
	 */
	public List<Map<String,String>> listCard(){
		
		List<Map<String,String>> list = new ArrayList<>();
		Map<String,String> map = Maps.newHashMap();
		map.put("id", "0");
		map.put("name", "全部");
		map.put("data", "0");
		map.put("lvl", "1");
		list.add(map);
		
		List<Map<String,String>> list2 = rptRuleConfigDAO.findCardName();
		
		list.addAll(list2);
	return list;
	}
	
	
    public List<Organization> listAllByUserForRule(Long userId) {
    	
        //首先 获取所有关联的地市
        List<Organization> relatedList = rptRuleConfigDAO.preForC3(userId);

        if (relatedList == null || relatedList.size() == 0) {
            throw new ReportException("该用户未关联地市组织");
        }

        List<Organization> list = new ArrayList<>();
        //然后拼接参数
        for (Organization o : relatedList) {
        	Long id = o.getId();
            String path = o.getParentIds();
            path = path == null ? id + "/%" : path + id + "/%";
            o.setParentIds(path);
            
            if(id ==5851){
            	Organization org = new Organization();
            	org.setData("0");
            	org.setLvl(1);
            	org.setId(id);
            	org.setName("股份");
            	list.add(org);
            	List<Organization> l =rptRuleConfigDAO.listAll();            	 
            	 for (Organization o2 : l){            		 
            		 Long id2 = o2.getId();
            		 List<Organization> list2 = rptRuleConfigDAO.listByRootAndLvl(id2); //最后进行判断所属地市 及子集
            		 list.addAll(list2);
            	 
            	 }
            }else{
            	list = rptRuleConfigDAO.listByRootAndLvl(id);
            }
  
        }

        return list;
    }
}
