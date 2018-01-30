package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.bean.RptImportDataGroup;
import com.loushuiyifan.report.dao.RptImportGroupDataDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportDataGroupVO;

@Service
public class ImportGroupService {
    private static final Logger logger = LoggerFactory.getLogger(ImportGroupService.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    RptImportGroupDataDAO rptImportGroupDataDAO;

    /**
     * 导入
     */
    @Transactional
    public void save(Path path,
                     Integer latnId,
                     Long userId) throws Exception {
        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportDataGroup> list = getRptImportDataGroup(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空，请检查后导入！: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        // 保存数据
        saveImportGroupDataByGroup(list, Math.toIntExact(userId), latnId);

    }


    /**
     * 稽核
     */
    public List<ImportDataGroupVO> list(Integer latnId, String groupId) {
    	List<ImportDataGroupVO> list = rptImportGroupDataDAO.listData(latnId, groupId);
    	
    	return list;
    }

    /**
     * 删除
     */
    public void delete(Integer latnId, String groupId) throws Exception {
        
         //指标编码groupId 为空时删除按latnId
        rptImportGroupDataDAO.deleteGroup(latnId, groupId);
    }

    /**
     * 保存excel数据
     */
    public void saveImportGroupDataByGroup(List<RptImportDataGroup> list,
                                           Integer userId,
                                           Integer latnId) throws Exception {
             
            for (RptImportDataGroup data : list) {
                //判断指标编码是否为空
                List<String> l = rptImportGroupDataDAO.findSubcode(data.getSubCode(), "1801", Long.parseLong("0"));
                if (l == null) {
                    throw new ReportException("导入的数据中，指标编码：" + data.getSubCode() + " 为非明细指标，请检查后重新导入！");
                } else {
                    data.setUserId(userId);
                    data.setLatnId(latnId);
                    data.setLstUpd(Date.from(Instant.now())); 
                    rptImportGroupDataDAO.insertSelective(data);
                }
            }
      
            logger.debug("批量插入结束"+ list.size());     
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

    static class RptImportDataGroupRead extends ReportReadServ<RptImportDataGroup> {

        protected List<RptImportDataGroup> processSheet(Sheet sheet) {
            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
            List<RptImportDataGroup> list = new ArrayList<RptImportDataGroup>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataGroup bean = new RptImportDataGroup();

                for (int x = startX; x <= row.getLastCellNum(); x++) {

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
                            bean.setSubCode(data);
                            break;

                        case 3:
                            bean.setSubName(data);
                            break;
                    }
                }
                
                //如果GroupId为空,则默认该行为无效数据
                if (bean.getGroupId() == null) {
                    continue;
                }
                list.add(bean);
            }

            return list;
        }

    }


}