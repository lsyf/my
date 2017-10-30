package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.RptImportCutRate;
import com.loushuiyifan.report.bean.RptImportDataCut;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportCutDataDAO;
import com.loushuiyifan.report.dao.RptImportCutRateDAO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.CutDataListVO;
import com.loushuiyifan.report.vo.CutRateVO;
@Service
public class ImportCutService {
	private static final Logger logger = LoggerFactory.getLogger(ImportCutService.class);
	@Autowired
    ExtImportLogDAO extImportLogDAO;

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    RptImportCutDataDAO rptImportCutDataDAO;
    @Autowired
    RptImportCutRateDAO rptImportCutRateDAO;
    
    /**
     * 导入
     * @param path
     * @param month
     * @param latnId
     * @param incomeSource
     * @param shareType
     * @param userName
     * @param remark
     */
	@Transactional
	public void save(Path path,
					String month,
					String latnId,
					String incomeSource,
					String shareType,
					String userName,
					String remark ){
		String filename = path.getFileName().toString();
    	
		try {
			//首先将文件解析成bean
			List<Map<String,Object>> list = getRptImportDataCut(path);
			//校验数据是否为空
			int size = list.size();
			if (size == 0){
	           String error = "文件数据为空: " + filename;
	           logger.error(error);
	           throw new ReportException(error);
			}
			//然后保存解析的数据   		
    		RptImportDataCut cut = new RptImportDataCut();
    		cut.setChgWho(userName);
    		cut.setShareType(Integer.parseInt(shareType));
    		cut.setLatnId(Integer.parseInt(latnId));
    		cut.setIncomeSource(incomeSource);
    		cut.setActiveFlag("Y");
    		
    		saveCutDataByGroup(list, month,cut);
    		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 查询数据
	 */
	public List<CutDataListVO> queryList(
			String month,
			Integer latnId,
            String incomeSource,
            Integer shareType
            ){
		String type = ReportConfig.RptImportType.CUT.toString();
		
		List<CutDataListVO> list =rptImportCutRateDAO
				.cutRateList(month,
							latnId,
							incomeSource,
							shareType,
							type);
				
		return list;
	}
	
	/**
     * 删除数据
     *
     */
    public void delete(	String month,
						Integer latnId,
			            String incomeSource,
			            Integer shareType,
			            String userName) {
    	StringBuilder sb = new StringBuilder();
		sb.append(latnId).append(incomeSource).append(shareType);
		String ruleId = sb.toString();
		try {
			RptImportDataCut cut = new RptImportDataCut();
			cut.setChgWho(userName);
			cut.setShareType(shareType);
			cut.setLatnId(latnId);
			cut.setIncomeSource(incomeSource);
			cut.setActiveFlag("N");
			List<String> check = rptImportCutDataDAO.checkCut(month, latnId, incomeSource, shareType, userName, ruleId);
	        if (check.size() != 0) {
	        	rptImportCutDataDAO.jihefaild(cut); 
	        	rptImportCutRateDAO.cutRateDel(latnId, incomeSource, shareType, userName,"N");
	        }else{
	        	throw new ReportException("您没有权限删除此记录");
	        }	
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReportException("删除导入时发生异常");
		}
    }
    
	/**
	 * 保存excel数据
	 */
	public void saveCutDataByGroup(	List<Map<String,Object>> list,
									String month,									
									RptImportDataCut cut
			                       ){
		final SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            logger.debug("批量插入数量: {}", list.size());
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<String,Object> temp : list) {
            	String sheetName = temp.get("name").toString();
            	String str = sheetName.substring(0, sheetName.indexOf("-"));
            	
            	sb = new StringBuilder();
            	cut = new RptImportDataCut();
            	
            	sb.append(cut.getLatnId()).append("-").append(cut.getIncomeSource()).append("-")
				.append(cut.getShareType()).append("-").append(str);// 形成ruleId
            	
            	cut.setRuleId(sb.toString());
            	cut.setGroupId(str);// 得到groupId
            	cut.setLstUpd(sdf2.format(new Date()));
            	logger.debug("----------------="+cut.getRuleId());
            	List<RptImportDataCut> l_cut = rptImportCutDataDAO.queryCut(sb.toString());           	
            	if(l_cut.size() ==0){
            		rptImportCutDataDAO.insert(cut); //插入切割比例日志
            	}else{
            		rptImportCutDataDAO.jihefaild(cut); //更新
            	}
            	
            	
    			Double result =rptImportCutRateDAO.cutRateJihetype(cut.getRuleId(),month);
    			if(result ==null){
    				@SuppressWarnings("unchecked") 
                	List<RptImportCutRate> l =(List<RptImportCutRate>)temp.get("data");           	
                	for(RptImportCutRate rate :l){ //插入切割数据
                		rate.setAcctMonth(month);
                		rate.setRuleId(cut.getRuleId());
                    	rptImportCutRateDAO.insertSelective(rate);	
                	}
    			}else {
    				throw new ReportException("该配置已存在，请先执行删除操作");
    			}
            	
            	//根据切割类型稽核
    			if(cut.getShareType() ==1){ //c4
    				Double result2 =rptImportCutRateDAO.cutRateJihetype(cut.getRuleId(),month);
    				if (result2 != 1) {
    					cut.setActiveFlag("N");
    					rptImportCutDataDAO.jihefaild(cut);
    					throw new ReportException("导入数据比例合计不等于1");
    				}
    			}else{
    				List<CutRateVO> l_rate =rptImportCutRateDAO.cutRateJihetype2(cut.getRuleId(),month);
    				if (l_rate.size() != 0) {
    					cut.setActiveFlag("N");
    					rptImportCutDataDAO.jihefaild(cut);
    					throw new ReportException("导入数据比例合计不等于1,请检查数据比例合计；\n若比例合计为1,请查询是否因多次导入造成数据重复，\n请先执行删除操作再尝试重新导入");
    				}    			   			   			
    			}
 	
            }
            sqlSession.commit();
        }catch (Exception e){
        		
        	// 若发生异常则删除导入的数据
        	rptImportCutRateDAO.cutRateDel(cut.getLatnId(),
        			                       cut.getIncomeSource(),
        			                       cut.getShareType(),        			                       
        			                       cut.getChgWho(),
        			                       "N");
        	rptImportCutDataDAO.jihefaild(cut);
        } finally {
            sqlSession.close();
            logger.debug("批量插入结束");
        }
	}
	
	/**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<Map<String,Object>> getRptImportDataCut(Path path) throws Exception {

        PoiRead read = new RptImportDataCutRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<Map<String,Object>> list = read.read();
       
        return list;
    }
    
    static class RptImportDataCutRead extends ReportReadServ<Map<String,Object>>{
    	
    	@Override
    	protected boolean checkSheet(Sheet sheet) {
    		boolean flag = super.checkSheet(sheet);
    		if(flag){
    			//通过sql，校验sheet名是否符合
    			
    		}
    		return false;
    	}
    	
    	@Override
    	protected List<Map<String,Object>> processSheet(Sheet sheet){
    		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();	
    		
    		List<Map<String,Object>> result = new ArrayList<>();
    		
    		Map<String,Object> map = Maps.newHashMap();
    		map.put("name", sheet.getSheetName());
    		List<RptImportCutRate> list = new ArrayList<RptImportCutRate>();
    		for (int y = startY; y <= sheet.getLastRowNum(); y++){
    			Row row = sheet.getRow(y);
    			RptImportCutRate bean = new RptImportCutRate();
    			for (int x = startX; x <= row.getLastCellNum(); x++){
    				String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    switch (x){
					case 0:
						bean.setAreaId(Integer.parseInt(data));						
						break;
					case 2:
						bean.setBureauId(Integer.parseInt(data));
						break;
					case 4:
						bean.setRate(Double.parseDouble(data));
						break;
					}    				
    			}
    			list.add(bean);
    		}
    		
    		map.put("data", list);
    		result.add(map);
    		return result;
    	}
    	
    }
    
    
    
    
}
