package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.loushuiyifan.report.bean.RptSettCutLog;
import com.loushuiyifan.report.bean.RptSettCutRate;
import com.loushuiyifan.report.dao.RptSettCutLogDAO;
import com.loushuiyifan.report.dao.RptSettCutRateDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.SettCutDataVO;
import com.loushuiyifan.report.vo.SettCutRateVO;

@Service
public class ImportSettleCutService {
    private static final Logger logger = LoggerFactory.getLogger(ImportSettleCutService.class);
    
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    RptSettCutRateDAO rptSettCutRateDAO;
    @Autowired
    RptSettCutLogDAO rptSettCutLogDAO;

    /**
     * 导入
     */
    @Transactional
    public void save(Path path, String month,String remark,Long userId) 
    	throws Exception {
        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptSettCutRate> list = getRptImportDataCut(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        //然后保存解析的数据
        Long logId =rptSettCutLogDAO.nextvalKey();
        saveCutDataByGroup(list, month, filename, userId, logId,remark);
        
        SPDataDTO dto = new SPDataDTO();
    	dto.setLogId(logId);
    	rptSettCutLogDAO.checkSettleData(dto);
    	Integer code = dto.getRtnCode();
    	if (code != 0) {//非0为失败
            throw new ReportException("1数据校验失败: " + dto.getRtnMsg());
        }

    }

    /**
     * 查询数据
     */
    public Map<String, Object> queryList(String month,String reportId) {

        List<SettCutRateVO> datas = rptSettCutRateDAO.SettCutRateData(month, reportId);
        List<SettCutDataVO> logs =rptSettCutLogDAO.SettCutLog(month);
        Map<String, Object> map = Maps.newHashMap();
        map.put("datas", datas);
        map.put("logs", logs);
        return map;
    }

    /**
     * 删除数据
     */
    public void delete(Long userId, Long logId) {
    	SPDataDTO dto = new SPDataDTO();
    	dto.setLogId(logId);
    	dto.setUserId(userId);
    	//TODO 存过待修改
    	rptSettCutLogDAO.delSettleData(dto);
    	Integer code = dto.getRtnCode();
    	if (code != 0) {//非0为失败
            throw new ReportException("1数据删除失败: " + dto.getRtnMsg());
        }
        
    }

    /**
     * 保存excel数据
     */
    public void saveCutDataByGroup(List<RptSettCutRate> list,
						    		String month,
						            String filename,
						            Long userId,
						            Long logId,
						            String remark) throws Exception {
    	
        RptSettCutLog log = new RptSettCutLog();
        log.setLogId(logId);
        log.setAcctMonth(month);
        log.setDetail(remark);
        log.setFileName(filename);
        log.setUserId(userId);
        log.setCount(list.size());
        log.setImportDate(Date.from(Instant.now()));
		rptSettCutLogDAO.insertSelective(log);
		
		for(RptSettCutRate rate : list){
			rate.setLogId(logId);
			rate.setAcctMonth(month);
			rate.setUserId(userId);
			rate.setLstUpd(Date.from(Instant.now()));
			rptSettCutRateDAO.insertSelective(rate);
		}
        
    }

    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<RptSettCutRate> getRptImportDataCut(Path path) throws Exception {

        PoiRead read = new RptImportDataCutRead()
			                .load(path.toFile())
			                .multi(true)//excel数据可以解析多sheet
			                .startWith(0, 1);

        List<RptSettCutRate> list = read.read();

        return list;
    }

    static class RptImportDataCutRead extends ReportReadServ<RptSettCutRate> {

        @Override
        protected boolean checkSheet(Sheet sheet) {
            boolean flag = super.checkSheet(sheet);
            if (flag) {
                //通过sql，校验sheet名是否符合
                return true;
            }
            return false;
        }

        @Override
        protected List<RptSettCutRate> processSheet(Sheet sheet) {
            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

            List<RptSettCutRate> list = new ArrayList<RptSettCutRate>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptSettCutRate bean = new RptSettCutRate();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    switch (x) {
                        case 0:
                            bean.setReportId(data);
                            break;
                        case 1:
                            bean.setReportName(data);
                            break;
                        case 2:
                            bean.setLatnId(Integer.parseInt(data));
                            break;
                        case 3:
                            bean.setLatnId(Integer.parseInt(data));
                            break;
                        case 4:
                            bean.setZbCode(data);
                            break;
                        case 5:
                            bean.setZbName(data);
                            break;
                        case 6:
                        	bean.setRate(Double.parseDouble(data));
        					break;

                    }
                }
                list.add(bean);
            }

            return list;
        }

    }


}
