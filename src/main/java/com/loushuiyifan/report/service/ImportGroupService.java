package com.loushuiyifan.report.service;

import com.alibaba.druid.util.StringUtils;
import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.bean.RptImportDataGroup;
import com.loushuiyifan.report.dao.RptImportGroupDataDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportReadServ;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ImportGroupService {
	private static final Logger logger = LoggerFactory.getLogger(ImportGroupService.class);

	@Autowired
    SqlSessionFactory sqlSessionFactory;
	@Autowired
	RptImportGroupDataDAO rptImportGroupDataDAO;
	
	/**
	 * 导入
	 * 
	 */
	@Transactional
	public void save(Path path,
			         String month,
			         Integer latnId,            		 
			         String userId ,
            		 String remark
            		 ){
		String filename = path.getFileName().toString();
		try {
			//首先将文件解析成bean
			List<RptImportDataGroup> list = getRptImportDataGroup(path);
			//校验数据是否为空
			int size = list.size();
			if (size == 0){
	           String error = "文件数据为空，请检查后导入！: " + filename;
	           logger.error(error);
	           throw new ReportException(error);
			}
		
			saveImportGroupDataByGroup(list, month, 
					Integer.parseInt(userId),
					 latnId, 
					(new Date()).toString()
					);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	/**
	 * 稽核
	 * 
	 */
	public List<String> list(){
		List<String> list =null;
	return list	;
	}
	
	/**
	 * 删除
	 */	
	public void delete(){
		
		
	}
	
	/**
	 * 保存excel数据
	 */
	public void saveImportGroupDataByGroup(	List<RptImportDataGroup> list,
									String month,
									Integer userId,
									Integer latnId ,
									String lstUpd
			                       ){
		final SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);	
		logger.debug("批量插入数量: {}", list.size());
		Long groupId = 0L;
		try {
			for(RptImportDataGroup data : list){
				groupId =data.getGroupId();
				List<String> l =rptImportGroupDataDAO.findSubcode(data.getSubCode());
				if(l ==null){
					 throw new ReportException("导入的数据中，指标编码：" + data.getSubCode() + " 为非明细指标，请检查后重新导入！");	
				}else{
					data.setUserId(userId);
					data.setLatnId(latnId);
					data.setLstUpd(lstUpd);
					rptImportGroupDataDAO.insertSelective(data);
					
				}
				
			}
			
			sqlSession.commit();
		} catch (Exception e) {
			logger.error("导入指标组配置时发生异常", e);
			try {
				rptImportGroupDataDAO.deleteGroup(latnId,groupId);
			} catch (Exception e2) {
				e.printStackTrace();
			}
			
			
		}finally {
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
    public List<RptImportDataGroup> getRptImportDataGroup(Path path) throws Exception {
    	
    		 PoiRead read = new RptImportDataGroupRead()
    	                .load(path.toFile())
    	                .multi(true)//excel数据可以解析多sheet
    	                .startWith(0, 1);

    		 List<RptImportDataGroup> list = read.read();
    	        
    	return list;        
    }
	
    static class RptImportDataGroupRead extends ReportReadServ<RptImportDataGroup>{
    	
    	protected  List<RptImportDataGroup> processSheet(Sheet sheet){
    		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();	
    		List<RptImportDataGroup> list = new ArrayList<RptImportDataGroup>();
    		for (int y = startY; y <= sheet.getLastRowNum(); y++){
    			Row row = sheet.getRow(y);
    			RptImportDataGroup bean = new RptImportDataGroup();
    			
    			for (int x = startX; x <= row.getLastCellNum(); x++){
    				
    				String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    
                    switch (x) {
					case 0:
						bean.setGroupId(Long.parseLong(data));
						break;

					case 1:
						bean.setGroupName(data);
						break;
					
	    			case 2:
						bean.setSubCode(Long.parseLong(data));
	    				break;
					
		    		case 3:
						bean.setSubName(data);
		    			break;
                    }
    			}
    			list.add(bean);
    		}
    		
    	return list;	
    	}
    	
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

