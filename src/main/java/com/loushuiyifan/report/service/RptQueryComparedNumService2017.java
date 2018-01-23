package com.loushuiyifan.report.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.util.ProtoStuffSerializerUtil;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ReportCache;
import com.loushuiyifan.report.bean.RptCase;
import com.loushuiyifan.report.dao.ReportCacheDAO;
import com.loushuiyifan.report.dao.RptQueryComparedNumDAO2017;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.FileService;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.serv.ReportExportServ;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.service.DictionaryService;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Service
public class RptQueryComparedNumService2017 {

    private static Logger logger = LoggerFactory.getLogger(RptQueryComparedNumService2017.class);

    @Autowired
    RptEditionService rptEditionService;

    @Autowired
    RptQueryComparedNumDAO2017 rptQueryComparedNumDAO;

    @Autowired
    LocalNetService localNetService;

    @Autowired
    CodeListTaxService codeListTaxService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    ReportDownloadService reportDownloadService;

    @Autowired
    RptCaseService rptCaseService;

    @Autowired
    ReportCacheDAO reportCacheDAO;

    //生成缓存状态
    ConcurrentHashMap<String, Instant> cacheStatusMap = new ConcurrentHashMap<>(3000);

    @Transactional
    public RptQueryDataVO list(String month,
                               String latnId,
                               String type,
                               Long userId) {

        // 查询报表实例是否存在
        //case(状态不为2)和cache同步，两者皆有或者两者皆无
        RptCase rptCase = rptCaseService.selectRptComparedNumCase(month, latnId, type);
        if (rptCase != null) {
            Long rptCaseId = rptCase.getRptCaseId();
            ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCaseId);
            byte[] data = cache.getHtmlData();
            RptQueryDataVO vo = ProtoStuffSerializerUtil.deserialize(data, RptQueryDataVO.class);
            return vo;

        }

        //判断是否正在生成
        String cacheStatusKey = month + latnId + type;
        Instant lastInst = cacheStatusMap.get(cacheStatusKey);
        if (lastInst != null) {
            Long during = Duration.between(lastInst, Instant.now()).getSeconds();
            throw new ReportException("正在生成数据，已耗时: " + during + "秒");
        }
        //设置为正在生成缓存状态
        cacheStatusMap.put(cacheStatusKey, Instant.now());
        RptQueryDataVO vo = new RptQueryDataVO();

        try {
            rptCase = new RptCase();
            rptCase.setAcctMonth(month);
            rptCase.setLatnId(Long.parseLong(latnId));
            rptCase.setProcessId(1101L);
            rptCase.setReportNo(1701L);
            rptCase.setCreateUserid(userId + "");
            rptCase.setTaxMark(Integer.parseInt(type));
            rptCase.setType(ReportConfig.RptExportType.RPT_QUERY_COMPARED_NUM.toString());//类型
            Long rptCaseId = rptCaseService.saveCaseSelective(rptCase);

            //列名
            List<Map<String, String>> cols = rptEditionService.listComeparedNumMap();
            //指标
            List<Map<String, String>> fields = rptEditionService.listFieldMap2017();
            //数据
            Map<String, Map<String, String>> datas = getDataMap(month, latnId, type);
            if (datas == null ||datas.size()==0) {
                throw new ReportException("数据还未准备好！");
            }
            //由于fields接下来会更改，优先生成文件
            String filePath = export(month, latnId, type,
                    cols, fields, datas);

            //生成文件后sftp推送到文件主机
            FileService.push(filePath);
            
            //生成html需求数据模型
            //首先遍历指标,建立 id->feild
            Map<String, Map<String, String>> fieldMap = Maps.newHashMapWithExpectedSize(2000);
            for (int i = 0; i < fields.size(); i++) {
                Map<String, String> temp = fields.get(i);
                fieldMap.put(temp.get("id"), temp);
            }

            //遍历数据，分别插入到指标数据中
            Iterator<String> it = datas.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String[] xy = key.split("_", 2);
                String x = xy[0];
                String y = xy[1];
                String v = datas.get(key).get("data");

                Map<String, String> temp = fieldMap.get(x);
                if (temp == null) {
                    continue;
                }
                temp.put(y, v);
            }

            vo.setTitles(cols);
            vo.setDatas(fields);


            // 保存缓存
            rptCaseService.saveReportCache(rptCaseId,
                    ProtoStuffSerializerUtil.serialize(vo),
                    filePath);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("查询数据失败: " + e.getMessage());
        } finally {
            //移除生成状态
            cacheStatusMap.remove(cacheStatusKey);
        }
        return vo;
    }

    public Map<String, Map<String, String>> getDataMap(String month, String latnId, String type) {
        LocalDate thisMonth = LocalDate.parse(month + "01", DateService.YYYYMMDD);
        String firstMonth = thisMonth.with(TemporalAdjusters.firstDayOfYear()).format(DateService.YYYYMM);
        String lastYearThisMonth = thisMonth.plusYears(-1).format(DateService.YYYYMM);
        String lastYearFirstMonth = thisMonth.plusYears(-1).with(TemporalAdjusters.firstDayOfYear()).format(DateService.YYYYMM);

        return rptQueryComparedNumDAO.listAsMap(lastYearFirstMonth, lastYearThisMonth,
                firstMonth, month, latnId, type);
    }

    /**
     * 根据已获取的数据生成文件
     */
    public String export(String month,
                         String latnId,
                         String type,
                         List<Map<String, String>> cols,
                         List<Map<String, String>> fields,
                         Map<String, Map<String, String>> data) throws Exception {
        String latnName = localNetService.getCodeName(latnId);
        if (latnName == null) {
            throw new ReportException("选择营业区错误,请重试");
        }

        //数据
        LinkedHashMap reportData = new LinkedHashMap<>();
        reportData.put(latnName, data);


        String templatePath = configTemplatePath();
        String outPath = configExportPath(month,
                latnName, type, false);

        ReportExportServ export = new RptQueryCustService.RptQueryExport();
        export.template(templatePath)
                .out(outPath)
                .column(cols)
                .row(fields)
                .data(reportData)
                .export();

        return outPath;
    }


    public String export(String month,
                         String latnId,
                         String type,
                         Boolean isMulti) throws Exception {

        //非多营业区 可从缓存表中判断是否已经生成数据
        if (!isMulti) {
            RptCase rptCase = rptCaseService.selectRptComparedNumCase(month, latnId, type);
            if (rptCase != null) {
                ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCase.getRptCaseId());
                String path = cache.getFilePath();
                
                //首先从文件主机下载文件
                FileService.pull(path);
                return path;
            }
        }

        List<Organization> orgs = localNetService.listUnderKids(latnId, isMulti);
        if (orgs == null || orgs.size() == 0) {
            throw new ReportException("选择营业区错误,请重试");
        }

        //客户群
        List<Map<String, String>> cols = rptEditionService.listComeparedNumMap();
        //指标
        List<Map<String, String>> fields = rptEditionService.listFieldMap2017();

        //数据
        LinkedHashMap reportData = new LinkedHashMap<>();
        for (Organization org : orgs) {
            String name = org.getName();
            String id = org.getData();
            Map<String, Map<String, String>> data = getDataMap(month, id, type);
            reportData.put(name, data);
        }

        String latnName = orgs.get(0).getName();


        String templatePath = configTemplatePath();
        String outPath = configExportPath(month,
                latnName, type, isMulti);


        ReportExportServ export = new RptQueryCustService.RptQueryExport();
        export.template(templatePath)
                .out(outPath)
                .column(cols)
                .row(fields)
                .data(reportData)
                .export();

        return outPath;
    }


    /**
     * 配置导出路径(包括文件名)
     *
     * @param month
     * @param latnName
     * @param type
     * @param isMulti
     * @return
     * @throws IOException
     */
    public String configExportPath(String month,
                                   String latnName,
                                   String type,
                                   Boolean isMulti) throws IOException {

        String sep = "_";
        String suffix;
        type = ReportConfig.getTaxType(type);

        if (isMulti) {
            suffix = "多营业区.xls";
        } else {
            suffix = "报表.xls";
        }

        String fileName = new StringBuffer("同期数报表")
                .append(sep).append(latnName)
                .append(sep).append(month)
                .append(sep).append(type)
                .append(sep).append(suffix)
                .toString();
        fileName.replace("*", "");
        Path path = Paths.get(reportDownloadService.configLocation(), month);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.resolve(fileName).toString();
    }

    /**
     * 配置模板路径
     * TODO 待整合(使用框架封装)
     *
     * @return
     */
    public String configTemplatePath() {
        String sep = File.separator;
        String templateName = dictionaryService.getKidDataByName(ReportConfig.RptExportType.PARENT.toString(),
                ReportConfig.RptExportType.RPT_QUERY_COMPARED_NUM.toString());
        return new StringBuffer(reportDownloadService.configTemplateLocation())
                .append(sep).append(templateName).toString();
    }

    public void remove(String month, String latnId, String type) {
        RptCase rptCase = rptCaseService.selectRptComparedNumCase(month, latnId, type);
        if (rptCase != null) {
            Long rptCaseId = rptCase.getRptCaseId();
            rptCaseService.removeCache(rptCaseId);
        }
    }

}
