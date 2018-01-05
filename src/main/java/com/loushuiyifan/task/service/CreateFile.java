package com.loushuiyifan.task.service;

import com.loushuiyifan.task.bean.FileInfoData;
import com.loushuiyifan.task.bean.FileXmlLogData;
import com.loushuiyifan.task.bean.RptMsgInfoData;
import com.loushuiyifan.task.bean.StatToTaxData;
import com.loushuiyifan.task.dao.FileInfoDataDAO;
import com.loushuiyifan.task.dao.FileXmlLogDataDAO;
import com.loushuiyifan.task.dao.RptMsgInfoDataDAO;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CreateFile {
	private static final Logger logger = LoggerFactory.getLogger(CreateXML.class);
	
	private static final String TEMP_PATH ="D:/report/files/createFile/";
	//private static final String TEMP_PATH ="/report/files/createFile/";
	private static final String FILE_PATH ="/app/MonthData/";
	public static final String IP = "134.96.93.186";
	public static final String USERNAME = "zjeda";
	
	@Autowired
	RptMsgInfoDataDAO rptMsgInfoDataDAO;
	@Autowired
	FileXmlLogDataDAO fileXmlLogDataDAO;
	@Autowired
	FileInfoDataDAO fileInfoDataDAO;
	public void process(String month){
		
		ArrayList<String> list = rptMsgInfoDataDAO.selectBatchIds(month);
        logger.info("tax to create file:" + list.size());
        if (list.size() == 0)
            return;
        int i = 0;
        for (String batchId : list) {
            i++;
            // 生成传文件次数
            String num = String.format("%05d", i);
            // 先置文件传输状态为
            ArrayList<String> temp = rptMsgInfoDataDAO.getStatus(batchId, month, "1");
            if (temp == null || temp.size() == 0 || !"R".equals(temp.get(0))) {
            	logger.info("files have created--" + batchId);
				continue;
			}
            // 先删除该批次已经生成的数据
			deleteFile(batchId, month);
			change_status("P1", batchId, month);
			logger.info("p1");
			
			int resultCode = 1;// 生成文件结果
			if ("DAPM_MSS_INCOME_YGZ_01".equals(batchId)) {
				resultCode = createFileInfo(batchId, 1L, month);
			} else if ("DAPM_MSS_INCOME_YGZ_02".equals(batchId)) {
				resultCode = createFileInfo(batchId, 2L, month);
			}
			
			CreateXML cxml = new CreateXML();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String time = sdf.format(Date.from(Instant.now()));
			String sessionId = "";
			StringBuilder sb = new StringBuilder();
			if ("DAPM_MSS_INCOME_YGZ_01".equals(batchId))
				sessionId =sb.append("EDA@571;").append(time).append(";").append(num)
				.append(";").append("05").toString();
				//sessionId = "EDA@571;" + time + ";" + num + ";" + "05";
			    
			else if ("DAPM_MSS_INCOME_YGZ_02".equals(batchId))
				//sessionId = "EDA@571;" + time + ";" + num + ";" + "06";
				sessionId =sb.append("EDA@571;").append(time).append(";").append(num)
				.append(";").append("06").toString();
			RptMsgInfoData info = new RptMsgInfoData();
			info.setSessionId(sessionId);
			info.setBatchId(batchId);
			info.setOriginHost("EDA@571");
			info.setDestHost("EDA@001");
			info.setBusiType("1");
			if ("DAPM_MSS_INCOME_YGZ_01".equals(batchId)) {
				info.setBusiService("200305");
			} else if ("DAPM_MSS_INCOME_YGZ_02".equals(batchId)) {
				info.setBusiService("200306");
			}

			info.setEventTime(time);
			info.setPath(FILE_PATH);
			info.setHostIp(IP);
			info.setUserName(USERNAME);
			info.setResultCode(null);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Document xml = cxml.createXML(info, month);
			FileXmlLogData log = new FileXmlLogData();
			log.setSessionId(sessionId);
			log.setAllXml(xml.asXML());
			log.setCreateTime(sdf2.format(Date.from(Instant.now())));
			fileXmlLogDataDAO.insertSelective(log);
			
			change_status("P2", batchId, month);
			logger.info("p2");
			if (resultCode == 0) {
				changeAllStatus(sessionId, "1");
			} else {
				changeAllStatus(sessionId, "10");
			}
        }	
		
	}
	
	public Integer createFileInfo(String batchId, Long rptType, String month){
		
		Long seq = rptMsgInfoDataDAO.getFileInfoseq();	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		FileOutputStream out =null;
		String fileName = null;
		String InterFaceType = "";
		String fileType = "1";
		int date_number = 0;
		String remark = "";
		String createTime =sdf.format(Date.from(Instant.now()));
		if (rptType == 1L) {
			InterFaceType = "DAPM_MSS_INCOME_YGZ_01";
			fileType = "1";
			remark = "007";
		} else if (rptType == 2L) {
			InterFaceType = "DAPM_MSS_INCOME_YGZ_02";
			fileType = "1";
			remark = "013";
		}
		
		FileInfoData inf = new FileInfoData();	
		inf.setFileId(seq);
		inf.setFileDate(month);
		ByteBuffer buf = null;
		RandomAccessFile ran =null; 
		List<Map<String,String>> list = rptMsgInfoDataDAO.listData(batchId, month);
		for(int j=0; j<list.size(); j++){
			inf.setNumber1(String.format("%02d", Integer.parseInt(list.get(j).get("sub").toString()) - 1));
			inf.setNumber2(String.format("%03d", Integer.parseInt(list.get(j).get("incomeSource").toString())));
			inf.setNumber3(String.format("%03d", Integer.parseInt(list.get(j).get("latnId").toString())));
			
			fileName =sb2.append(InterFaceType).append(".").append(createTime).append(".")
					.append(month).append(".").append(inf.getNumber1()).append(".").append(inf.getNumber2())
					.append(".").append(inf.getNumber3()).append(".833").toString();
			
			/* 生成DAT文件 */	
			Path path = Paths.get(TEMP_PATH +fileName +".DAT");
			File f = new File(TEMP_PATH +fileName +".DAT");			
			//out =new FileOutputStream(f);
			byte b1[] = { 0x05 };
			String str1 = new String(b1);
			try {
				buf = ByteBuffer.allocate(1024);
				ran =new RandomAccessFile(f,"rw");
				List<StatToTaxData> list2 = rptMsgInfoDataDAO.listStatTaxForMap(month, remark);
				logger.info("Stat_to_tax数量:" + list.size());
				for (StatToTaxData temp : list2) {
					
					sb.append(nvl(temp.getDocumSerNbr())).append(str1).append(temp.getVoucherRowNbr()).append(str1).append(temp.getGjahr()).append(str1)
					  .append(temp.getMonat()).append(str1).append(temp.getBukrs()).append(str1).append(temp.getPrctr()).append(str1).append(temp.getWwa04())
					  .append(str1).append(temp.getNssbh()).append(str1).append(temp.getWwa09()).append(str1).append(temp.getSwkm()).append(str1)
					  .append(nvl(temp.getSfyywsr())).append(str1).append(nvl(temp.getSrlx())).append(str1).append(nvl(temp.getSysz())).append(str1)
					  .append(nvl(temp.getSbfs())).append(str1).append(nvl(temp.getJsff())).append(str1).append(nvl(temp.getYslb())).append(str1)
					  .append(nvl(temp.getSfjzjt())).append(str1).append(temp.getSlv()).append(str1)
					  .append(temp.getCrncyAmt().doubleValue()).append(str1)
					  .append(temp.getSwsrje().doubleValue()).append(str1)
					  .append(temp.getSwsrse().doubleValue()).append(str1)
					  .append(temp.getSfstxs()).append(str1).append(temp.getSwtzlx()).append(str1)
					  .append(temp.getSwtzyy()).append(str1).append(temp.getJfsssj()).append(str1)
					  .append(temp.getJsswkn()).append(str1).append(temp.getJfsl());
					
					//buf.put(getBytes(sb.toString()));  
					FileChannel fc = ran.getChannel(); 
					buf.clear();
					buf.put(sb.toString().getBytes());
					buf.flip();
					buf.rewind();
					
					while(buf.hasRemaining()){
						fc.write(buf);
						fc.close();
						buf.clear();
					}								    
				}
											    
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			inf.setFileSize(f.length());
			inf.setFileNumber(date_number);
			inf.setFileType("DAT");
			inf.setRptType(fileType);
			inf.setInterFaceType(batchId);
			fileInfoDataDAO.insertSelective(inf);
			
			//生成校验文件
			File val = new File(TEMP_PATH +fileName +".VAL");			
			byte b[] = { 0x05 };
			String str = new String(b);
			try {
				buf = ByteBuffer.allocate(1024);
				out =new FileOutputStream(val);
				sb.append(fileName).append(str).append(".DAT").append(str).append(inf.getFileSize())
				.append(str).append(inf.getFileNumber()).append(str).append(month).append(str).append(createTime);
				
				FileChannel fc = out.getChannel();
				buf.put(sb.toString().getBytes());
				buf.flip();
				fc.write(buf);
				
				fc.close();
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(out !=null){
					try {  
	                    out.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
				}
			}
			
			inf.setFileSize(f.length());
			inf.setFileNumber(date_number);
			inf.setFileType("VAL");
			inf.setRptType(fileType);
			inf.setInterFaceType(batchId);
			fileInfoDataDAO.insertSelective(inf);
			
			//ftp成功
			
		}
				
		return 0;
	}
	
	
	public String nvl(String s) {
		if (null == s)
			s = "";
		return s;
	}
	private byte[] getBytes (String chars) {//将字符转为字节(编码)
		   Charset cs = Charset.forName("UTF-8");
		   CharBuffer cb = CharBuffer.allocate (chars.length());
		   cb.put (chars);
		   cb.flip ();
		   ByteBuffer bb = cs.encode(cb);
		   return bb.array();
	}

	private char[] getChars(byte[] bytes) {//将字节转为字符(解码)
	      Charset cs = Charset.forName ("UTF-8");
	      ByteBuffer bb = ByteBuffer.allocate (bytes.length);
	      bb.put (bytes);
	      bb.flip ();
	      CharBuffer cb = cs.decode(bb);
	  
	   return cb.array();
	}
	
	private void deleteFile(String batchId, String month) {
		// 删除rpt_msg_info
    	rptMsgInfoDataDAO.delRptMsgInfo(batchId, month, "1");
    	
		// 删除file_info
    	rptMsgInfoDataDAO.deleteFileInfo(batchId, month, "1");
		
		logger.info("-deleteFile success!");
	}
    
    public void change_status(String status, String batchId, String month) {
    	rptMsgInfoDataDAO.changeStatus(batchId, month, status);
    }
	
    public void changeAllStatus(String sessionId, String status) {
    	rptMsgInfoDataDAO.changeStatusById(sessionId, status);
	}
}
