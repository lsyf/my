package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
import com.loushuiyifan.report.bean.RptImportCutRate;
import com.loushuiyifan.report.bean.RptImportDataCut;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportCutDataDAO;
import com.loushuiyifan.report.dao.RptImportCutRateDAO;
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
     */
    @Transactional
    public void save(Path path,
                     String month,
                     Integer latnId,
                     String incomeSource,
                     Integer shareType,
                     String username
    ) throws Exception {
        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<Map<String, Object>> list = getRptImportDataCut(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        //然后保存解析的数据
        saveCutDataByGroup(list, month, username, latnId, incomeSource, shareType);


    }

    /**
     * 查询数据
     */
    public List<CutDataListVO> queryList(String month,
                                         Integer latnId,
                                         String incomeSource,
                                         Integer shareType,
                                         String remark) {

        List<CutDataListVO> list = rptImportCutRateDAO.cutRateList(month,
                latnId,
                incomeSource,
                shareType,
                remark);
        if (list == null ||list.size()==0) {
            throw new ReportException("查询数据为空！");
        }
        return list;
    }

    /**
     * 删除数据
     */
    public void delete(String month,
                       Integer latnId,
                       String incomeSource,
                       Integer shareType,
                       Long userId,
                       String userName) {

        try {
        	String flag ="";
        	List<String> role =rptImportCutDataDAO.selectRoleById(userId);
        	if(role.contains("1") ||role.size()==1){
        		flag ="1";
        		rptImportCutDataDAO.updataCutFlag(latnId, incomeSource, shareType,userName,"N");
        		rptImportCutRateDAO.cutRateDel(latnId, incomeSource, shareType,flag, userName);

        	}else{
        		//List<String> check = rptImportCutDataDAO.checkCut(month, latnId, incomeSource, shareType, userName);
                //if (check.size() != 0) {
                    rptImportCutDataDAO.updataCutFlag(latnId, incomeSource, shareType,userName,"N");
                    rptImportCutRateDAO.cutRateDel(latnId, incomeSource, shareType,"", userName);
               // } else {
                //    throw new ReportException("您没有权限删除此记录");
               // }
        	}
        	
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("删除导入时发生异常:" + e.getMessage());
        }
    }

    /**
     * 保存excel数据
     */
    public void saveCutDataByGroup(List<Map<String, Object>> list,
                                   String month,
                                   String username,
                                   Integer latnId,
                                   String incomeSource,
                                   Integer shareType
    ) throws Exception {
        String msg = null;
        try {
            Date now = Date.from(Instant.now());
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> temp = list.get(i);
                String sheetName = temp.get("name").toString();
                String str = sheetName.substring(0, sheetName.indexOf("-"));

                RptImportDataCut cut = new RptImportDataCut();
                cut.setChgWho(username);
                cut.setShareType(shareType);
                cut.setLatnId(latnId);
                cut.setIncomeSource(incomeSource);
                cut.setActiveFlag("Y");

                // 形成ruleId
                StringBuffer sb = new StringBuffer();
                sb.append(latnId).append("-")
                        .append(incomeSource).append("-")
                        .append(shareType).append("-")
                        .append(str);
                String ruleId = sb.toString();

                cut.setRuleId(ruleId);
                cut.setGroupId(Long.parseLong(str));// 得到groupId
                cut.setLstUpd(now);

                //首先查询表里是否存在数据
                RptImportDataCut exist_cut = rptImportCutDataDAO.selectByPrimaryKey(ruleId);
                if (exist_cut == null) {
                    rptImportCutDataDAO.insertSelective(cut);
                } else {//如果记录数据已存在 则更新状态和更新lst_upd
                	rptImportCutDataDAO.updataCutFlag(latnId,
                            incomeSource,
                            shareType,
                            username,
                            "Y");
                }

                //统计切割数据为null
                Double sum = rptImportCutRateDAO.calcRateSum(ruleId, month);
                if (sum == null) {
                    List<RptImportCutRate> l = (List<RptImportCutRate>) temp.get("data");
                    for (RptImportCutRate rate : l) { //插入切割数据
                        rate.setAcctMonth(month);
                        rate.setRuleId(ruleId);
                        rptImportCutRateDAO.insertSelective(rate);
                    }
                } else {
                    throw new ReportException("该配置已存在，请先执行删除操作");
                }

                //根据切割类型稽核  
                if (shareType == 1) { //c4
                    Double result2 = rptImportCutRateDAO.calcRateSum(cut.getRuleId(), month);
                    if (result2 != 1) {

                        rptImportCutDataDAO.updataCutFlag(latnId, incomeSource, shareType,username,"N");
                        throw new ReportException("导入数据比例合计不等于1");
                    }
                } else {
                    List<CutRateVO> l_rate = rptImportCutRateDAO.sumRateByRuleId(cut.getRuleId(), month);
                    if (l_rate.size() != 0) {
                        rptImportCutDataDAO.updataCutFlag(latnId, incomeSource, shareType,username,"N");
                        throw new ReportException("导入数据比例合计不等于1,请检查数据比例合计；\n若比例合计为1,请查询是否因多次导入造成数据重复，\n请先执行删除操作再尝试重新导入");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("4保存数据失败", e);
            msg = e.getMessage();
        } finally {
            if (msg != null) {
                // 若发生异常则删除导入的数据
                rptImportCutRateDAO.cutRateDel(latnId, incomeSource, shareType,"", username);
                rptImportCutDataDAO.updataCutFlag(latnId, incomeSource, shareType,username,"N");
                throw new ReportException(msg);
            }
        }
    }

    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<Map<String, Object>> getRptImportDataCut(Path path) throws Exception {

        PoiRead read = new RptImportDataCutRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<Map<String, Object>> list = read.read();

        return list;
    }

    static class RptImportDataCutRead extends ReportReadServ<Map<String, Object>> {

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
        protected List<Map<String, Object>> processSheet(Sheet sheet) {

            List<Map<String, Object>> result = new ArrayList<>();

            Map<String, Object> map = Maps.newHashMap();
            map.put("name", sheet.getSheetName());
            List<RptImportCutRate> list = new ArrayList<RptImportCutRate>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportCutRate bean = new RptImportCutRate();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getXLSCellValue(row.getCell(x));
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    switch (x) {
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
