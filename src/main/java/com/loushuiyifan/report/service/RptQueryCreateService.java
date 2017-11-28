package com.loushuiyifan.report.service;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.report.bean.CodeListTax;
import com.loushuiyifan.report.bean.RptExcelWyf;
import com.loushuiyifan.report.dao.RptQueryCreateDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.system.service.DictionaryService;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 漏水亦凡
 * @date 2017/11/24
 */
@Service
public class RptQueryCreateService {
    private static Logger logger = LoggerFactory.getLogger(RptQueryCustService.class);

    @Autowired
    CodeListTaxService codeListTaxService;

    @Autowired
    LocalNetService localNetService;

    @Autowired
    DictionaryService dictionaryService;


    @Autowired
    RptQueryCreateDAO rptQueryCreateDAO;

    @Autowired
    RptQueryCustService rptQueryCustService;

    @Autowired
    ReportDownloadService reportDownloadService;

    //生成文件状态
    ConcurrentHashMap<String, Instant> cacheStatusMap = new ConcurrentHashMap<>(3000);


    public List<RptExcelWyf> list(String month, String latnId, String incomeSource) {
        List<RptExcelWyf> list = rptQueryCreateDAO.list(month, latnId, incomeSource);
        return list;
    }

    public void remove(Long[] excelIds) {
        rptQueryCreateDAO.remove(excelIds);
    }

    public void create(String month, String latnId, String incomeSource) {
        //判断是否正在生成
        String statusKey = month + latnId + incomeSource;
        Instant lastInst = cacheStatusMap.get(statusKey);
        if (lastInst != null) {
            Long during = Duration.between(lastInst, Instant.now()).getSeconds();
            throw new ReportException("正在生成数据，已耗时: " + during + "秒");
        }
        //设置为正在生成缓存状态
        cacheStatusMap.put(statusKey, Instant.now());

        ExecutorService executorService = null;
        try {

            //首先获取指定收入来源
            String type = "income_source2017";
            List<CodeListTax> incomeSources = new ArrayList<>();
            if ("0".equals(incomeSource)) {
                incomeSources.addAll(codeListTaxService.listKidsByTypeAndData(type, "02"));
                incomeSources.addAll(codeListTaxService.listKidsByTypeAndData(type, "50"));
            } else {
                incomeSources.addAll(codeListTaxService.listKidsByTypeAndData(type, incomeSource));
            }
            logger.info("一键生成收入来源数: {}", incomeSources.size());

            //然后获取指定地市
            List<Organization> orgs = new ArrayList<>();
            if (StringUtils.isEmpty(latnId)) {
                orgs.addAll(localNetService.listAllCity());
                orgs.remove(0);//不生成股份数据
            } else {
                orgs.add(localNetService.getName(latnId));
            }
            logger.info("一键生成地市数: {}", orgs.size());

            //生成数据(含税，增值税，不含税) 多营业区
            List<Task> tasks = new ArrayList<>();
            String[] taxTypes = {"0", "1", "2"};
            for (Organization org : orgs) {
                for (CodeListTax is : incomeSources) {
                    for (String taxType : taxTypes) {
                        Task task = new Task();
                        task.setMonth(month);
                        task.setLatnId(org.getData());
                        task.setLatnName(org.getName());
                        task.setIncomeSource(is.getCodeName());
                        task.setIncomeSourceId(is.getCodeId());
                        task.setParentIncomeId(is.getParentCodeId());
                        task.setTaxType(taxType);
                        tasks.add(task);
                    }
                }
            }

            int num = tasks.size();
            logger.info("一键生成文件数:{}", num);


            String config = dictionaryService
                    .getKidDataByName("rptQueryCreate", "多线程配置");
            String[] params = config.split("_");
            int corePoolSize = Integer.parseInt(params[0]);
            int maximumPoolSize = Integer.parseInt(params[1]);
            logger.info("一键生成线程池配置:{},{}", corePoolSize, maximumPoolSize);

            //使用线程池
            executorService = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize,
                    100,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(num));
            CompletionService<String> completionService =
                    new ExecutorCompletionService(executorService);

            for (int i = 0; i < num; i++) {
                completionService.submit(tasks.get(i));
            }

            for (int i = 0; i < num; i++) {
                String path = completionService.take().get();
                logger.debug("文件生成成功:{}", path);
            }

        } catch (Exception e) {
            logger.error("一键生成失败:", e);
            throw new ReportException("一键生成失败:" + e.getMessage());
        } finally {
            //移除生成状态
            cacheStatusMap.remove(statusKey);
            if (executorService != null) {
                executorService.shutdown();
            }
        }

    }

    /**
     * 一键下载界面的 根据用户查询
     *
     * @param month
     * @param userId
     * @return
     */
    public List<RptExcelWyf> listByUser(String month, Long userId) {
        List<Organization> orgs = localNetService.listForC4(userId);
        List<RptExcelWyf> list = rptQueryCreateDAO.listByUser(month, orgs);
        return list;
    }

    public String downloadZip(Long[] excelIds) {
        String dir = reportDownloadService.configLocation();
        String name = String.format("report%s.zip",
                LocalDateTime.now().format(DateService.YYYYMMDDHHMMSS));

        Path path = Paths.get(dir, name);

        try (ZipOutputStream zos =
                     new ZipOutputStream(new FileOutputStream(path.toFile()))
        ) {
            for (int i = 0; i < excelIds.length; i++) {
                long excelId = excelIds[i];
                String filePath = getFilePath(excelId);

                File f = new File(filePath);
                if (!f.exists()) {
                    continue;
                }

                BufferedInputStream bis = new BufferedInputStream(
                        new FileInputStream(f));
                String filename = FilenameUtils.getName(filePath);

                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);

                int len;
                byte[] buffer = new byte[8192];
                while ((len = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                bis.close();
            }
            zos.close();
        } catch (Exception e) {
            logger.error("压缩文件失败", e);
            throw new ReportException("压缩文件失败" + e.getMessage());
        }

        return path.toString();
    }

    public String getFilePath(Long excelId) {
        return rptQueryCreateDAO.selectByPrimaryKey(excelId).getFilePath();
    }

    @Data
    class Task implements Callable<String> {
        String month;
        String latnId;
        String incomeSourceId;
        String taxType;

        String latnName;
        String incomeSource;
        String parentIncomeId;

        @Override
        public String call() throws Exception {
            logger.info("一键生成文件:{}-{}-{}-{}", month, latnName, incomeSource, taxType);

            String path;
            try {
                path = rptQueryCustService.export(month,
                        latnId,
                        incomeSourceId,
                        taxType,
                        true);
            } catch (Exception e) {
                logger.error("文件生成失败:", e);
                path = "文件生成失败";
            }

            //插入生成数据
            RptExcelWyf bean = new RptExcelWyf();
            bean.setAcctMonth(month);
            bean.setAreaName(latnName);
            bean.setIncomeSource(incomeSource);
            bean.setIncomesourceId(incomeSourceId);
            bean.setPraentIncomId(parentIncomeId);
            bean.setFilePath(path);
            bean.setFileName(FilenameUtils.getName(path));
            bean.setCreateTime(LocalDateTime.now().format(DateService.YYYY_MM_DD_HH_MM_SS));
            rptQueryCreateDAO.insert(bean);

            return path;
        }
    }
}

