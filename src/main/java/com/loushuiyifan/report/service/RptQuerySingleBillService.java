package com.loushuiyifan.report.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.controller.RptFundsFeeAuditController;
import com.loushuiyifan.report.dao.RptQuerySingleBillDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.vo.SingleBillVO;

@Service
public class RptQuerySingleBillService {
	private static final Logger logger = LoggerFactory.getLogger(RptQuerySingleBillService.class);

	@Autowired
	RptQuerySingleBillDAO rptQuerySingleBillDAO;
	
	
	public Map<String,Object> list(String month,String latnId,String phone){
		
		Map<String,Object> map = new HashMap<>();
			Integer months = Integer.parseInt(month);
			Integer latnIds = Integer.parseInt(latnId);
		    map.put("month",months);
			map.put("latnId", latnIds);
			map.put("phone", phone);
		rptQuerySingleBillDAO.listBill(map);
		
		List<SingleBillVO> list = (List<SingleBillVO>)map.get("list");
		if (list == null || list.size() == 0) {
            throw new ReportException("查询信息为空");
        }
		
		
		Map param = new HashMap();
		for(SingleBillVO vo: list){			
			String subCode =vo.getSubCode();
			Map m= (HashMap)param.get(subCode);
			if(m ==null){
				m = new HashMap();
				m.put("phoneId", vo.getPhoneId());
				m.put("subCode", vo.getSubCode());
				m.put("verticalIndexName", vo.getVerticalIndexName());
				m.put("unpayFee", vo.getUnpayFee());				
				param.put(subCode, m);

			}else{
				String unpay =m.get("unpayFee").toString();
				BigDecimal unpayFee = new BigDecimal(unpay) ;
				unpayFee =unpayFee.add(BigDecimal.valueOf(vo.getUnpayFee()));
				m.put("unpayFee", unpayFee);
				
			}			
		}
		
		List ls =new ArrayList();
		Iterator it = param.keySet().iterator();
		while (it.hasNext())
		ls.add(param.get(it.next()));
		
		logger.info("list2="+ls.size());	
		Map<String,Object> mlist = new HashMap<>();
		mlist.put("list", list);
		mlist.put("list2", ls);
		return mlist;
	}
}
