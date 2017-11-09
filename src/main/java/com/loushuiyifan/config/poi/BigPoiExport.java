//package com.loushuiyifan.config.poi;
//
//import lombok.Data;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * @author 漏水亦凡
// * @create 2016-12-08 16:26.
// */
//public class BigPoiExport extends AbstractPoiExport<Map<String, Object>> {
//    private String[] keys = null;
//    private String outPathDir = null;//输出路径
//    private String outFileName = null;//输出文件名(预设)
//
//    private List<String> outPaths = new ArrayList<>();//全部文件名
//    private int rowStart = 0;
//    private int columnStart = 0;
//    private int rowMax = 200000;
//    private Map<String, Object> param;
//
//    private BigPoiService bigPoiService;
//
//    private ExportResult exportResult = new ExportResult();
//
//    private static String defaultOutPathDir;//默认输出路径
//    private static byte[] lock = new byte[0];//锁定生成时间目录代码块
//
//    static {
//        Properties pro = new Properties();
//        try {
//            pro.load(BigPoiExport.class.getResourceAsStream("/project.properties"));
//            if ("\\".equals(File.separator)) {
//                defaultOutPathDir = pro.getProperty("uploadFileDir_w");
//            } else {
//                defaultOutPathDir = pro.getProperty("uploadFileDir_l");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public BigPoiExport(String inPath, String outPathDir, String outFileName, String[] keys) {
//        super(inPath, outFileName, null);
//        this.keys = keys;
//        this.outPathDir = outPathDir;
//        this.outFileName = outFileName;
//    }
//
//    public BigPoiExport(String inPath, String outFileName, String[] keys) {
//        this(inPath, null, outFileName, keys);
//    }
//
//    public BigPoiExport(String outFileName, String[] keys) {
//        this(null, outFileName, keys);
//    }
//
//    public BigPoiExport() {
//
//    }
//
//    /**
//     * 设置起点
//     *
//     * @param rowStart
//     * @param columnStart
//     * @return
//     */
//    public BigPoiExport startWith(int rowStart, int columnStart) {
//        this.rowStart = rowStart;
//        this.columnStart = columnStart;
//        return this;
//    }
//
//    public BigPoiExport setRowMax(int rowMax) {
//        this.rowMax = rowMax;
//        return this;
//    }
//
//    public BigPoiExport setBigPoiService(BigPoiService bigPoiService) {
//        this.bigPoiService = bigPoiService;
//        return this;
//    }
//
//    public BigPoiExport setParam(Map<String, Object> param) {
//        this.param = param;
//        return this;
//    }
//
//    /**
//     * 处理逻辑
//     *
//     * @throws Exception
//     */
//    @Override
//    protected void process(Workbook wb) throws Exception {
//        if (keys == null || keys.length == 0) {
//            exportResult.setStatus(ExportResult.FAIL);
//            throw new Exception("EXCEL对应的列的KEY为空!");
//        }
//
//        //仅导出到第一个sheet
//        Sheet sheet = null;
//        if (hasTemp) {
//            sheet = wb.getSheetAt(0);
//        } else {
//            sheet = wb.createSheet("temp");
//        }
//        int rowIndex = rowStart;//行指针
//        int columnIndex = columnStart;//列指针
//        for (Map<String, Object> map : list) {
//            Row row = sheet.createRow(rowIndex);
//            for (String key : keys) {
//                Cell cell = row.createCell(columnIndex);
//                String value = "";
//                if (map.get(key) != null) {
//                    value = String.valueOf(map.get(key));
//                }
//                cell.setCellValue(value);
//                columnIndex++;
//            }
//            rowIndex++;
//            columnIndex = columnStart;
//        }
//
//        //导出完成后清空数据
//        this.list = null;
//    }
//
//    @Override
//    public void export() throws Exception {
//        int allResultNum = bigPoiService.getSize(param);
//        int fileNum = allResultNum % rowMax > 0 ? (allResultNum / rowMax) + 1 : allResultNum / rowMax;
//
//        if (allResultNum == 0) {//无记录
//            exportResult.setStatus(ExportResult.SUCCESS_NODATA);
//            exportResult.setResultInfo("查询无记录");
//            return;
//        }
//
//        //预先定义所有输出文件
//        String dir = handleOutPathDir();
//        String filterName = outFileName;
//        String suffix = "xlsx";
//        if (outFileName != null && outFileName.lastIndexOf(".") != -1) {
//            filterName = outFileName.substring(0, outFileName.lastIndexOf("."));
//            suffix = outFileName.substring(outFileName.lastIndexOf("."));
//        }
//        for (int i = 0; i < fileNum; i++) {
//            if (fileNum == 1) {
//                outPaths.add(String.format("%s%s%s%s", dir, File.separator, filterName, suffix));
//            } else {
//                outPaths.add(String.format("%s%s%s%d%s", dir, File.separator, filterName + "_", i + 1, suffix));
//            }
//        }
//
//        int from = 0;
//        for (String path : outPaths) {
//            this.outPath = path;
//            this.list = bigPoiService.getResult(param, from, rowMax);
//
//            super.export();
//
//            from = from + rowMax;
//        }
//
//		/*创建并打包文件*/
//        String zipFileFullName = dir + File.separator + filterName + ".zip";
//        File zip = new File(zipFileFullName);// 压缩文件
//        if (!zip.exists()) {
//            zip.createNewFile();
//        }
//        File srcfile[] = new File[outPaths.size()];
//        for (int i = 0; i < outPaths.size(); i++) {
//            srcfile[i] = new File(outPaths.get(i));
//        }
//        ZipFilesUtil.zipFiles(srcfile, zip, true);
//
//		/*保存导出路径及文件信息*/
//        exportResult.setFileFullPath(replaceFilePath(zipFileFullName));
//        exportResult.setFilePath(replaceFilePath(dir));
//        exportResult.setFileName(filterName + ".zip");
//        exportResult.setStatus(ExportResult.SUCCESS_HASDATA);
//    }
//
//    public ExportResult getExportResult() {
//        return exportResult;
//    }
//
//    private String replaceFilePath(String s) {
//        return s.replace("\\", "/");
//    }
//
//    /**
//     * <p>Description: 处理输出路径</p>
//     * <p>Title: handleOutPathDir</p>
//     *
//     * @return
//     */
//    private String handleOutPathDir() {
//        String dir = "";
//        if (outPathDir == null) {//输出路径为空,获取默认的输出路径
//            dir = defaultOutPathDir;
//        } else {
//            dir = outPathDir;
//        }
//        if (!dir.endsWith(File.separator)) {
//            dir += File.separator;
//        }
//        String filterName = outFileName;
//        if (outFileName != null && outFileName.lastIndexOf(".") != -1) {
//            filterName = outFileName.substring(0, outFileName.lastIndexOf("."));
//        }
//
//        do {
//            dir += filterName + File.separator + getTimeStr();
//
//            File filePath = new File(dir);
//            if (!filePath.exists()) {//目录不存在,创建
//                filePath.mkdirs();
//                break;
//            }
//        } while (true);
//
//        return dir;
//    }
//
//    private String getTimeStr() {
//        String timeStr;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        synchronized (lock) {//加锁
//            timeStr = sdf.format(new Date());
//            try {
//                Thread.sleep(1);//区分开时间段
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return timeStr;
//    }
//
//    /**
//     * 海量数据导出服务
//     */
//    public interface BigPoiService {
//        /**
//         * 预输出大小
//         */
//        int getSize(Map<String, Object> param);
//
//        /**
//         * 分页结果
//         */
//        List<Map<String, Object>> getResult(Map<String, Object> param, int from, int limit);
//    }
//
//    @Data
//    public class ExportResult {
//        public static final int SUCCESS_HASDATA = 1;
//        public static final int SUCCESS_NODATA = 2;
//        public static final int FAIL = 3;
//
//        private String fileFullPath;//文件全路径
//        private String filePath;//文件路径
//        private String fileName;//文件名称
//        private String resultInfo;//结果信息
//        private int status;//1:成功,有数据
//
//
//    }
//}
