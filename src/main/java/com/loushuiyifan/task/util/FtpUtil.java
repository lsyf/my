package com.loushuiyifan.task.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class FtpUtil {
	private static Logger log = Logger.getLogger(FtpUtil.class);
	private static Logger IP =null;
	private static Logger USERNAME =null;
	private static Logger PASSWORD =null;
	private static Logger FILE_PATH =null;
		
		
	public static void main(String[] args) throws Exception {
		
	}
	
	//sftp上传文件
	public static boolean uploadFile(String ip, String name, String password, String remoteDir, String dir)
		throws JSchException{
		boolean flag =false;
		
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
	
		session = jsch.getSession(name, ip, 22);
		session.setPassword(password);
		session.setTimeout(600000);
		//session.setClientVersion("StrictHostKeyChecking");		
		Properties pro = new Properties();
		pro.put("StrictHostKeyChecking", "no");  
        session.setConfig(pro);  
        session.connect();
				
        channel =(Channel)session.openChannel("sftp");
        channel.connect(1000);
        ChannelSftp sftp = (ChannelSftp)channel;
        log.info("sftp传输文件开始。。。。。。");
        
		try {
			 if (!sftp.isConnected()){
				 log.error("Failed to connect FTP server! :" + ip);
				 return false;
			 } 
			// 225目录文件上传到远程目录
			sftp.put(dir, remoteDir, ChannelSftp.OVERWRITE);
			log.info("sftp传输文件结束！！！");	
			flag =true;
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("upload error!");  
		}finally{			
			sftp.getSession().disconnect();
			sftp.quit(); 
			session.disconnect();
			channel.disconnect();
		}
		return flag;
	}
	

	//sftp 根据文件名下载文件
	public static boolean downloadFile(String ip, String name, String password, String remoteDir, String dir,String fileName)
		throws JSchException{
		boolean flag =false;
		
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
	
		session = jsch.getSession(name, ip, 22);
		session.setPassword(password);
		session.setTimeout(600000);
		//session.setClientVersion("StrictHostKeyChecking");		
		Properties pro = new Properties();
		pro.put("StrictHostKeyChecking", "no");  
        session.setConfig(pro);  
        session.connect();
				
        channel =(Channel)session.openChannel("sftp");
        channel.connect(1000);
        ChannelSftp sftp = (ChannelSftp)channel;
        log.info("sftp传输文件开始。。。。。。");
        
		try {
			 if (!sftp.isConnected()){
				 log.error("Failed to connect FTP server! :" + ip);
				 return false;
			 } 
			
			File file = new File(dir+fileName);
			FileOutputStream  os= new FileOutputStream(file);
			sftp.get(remoteDir+fileName, os);
			
			log.info("sftp传输文件结束！！！");	
			flag =true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("download error!");  
		}finally{
			
			sftp.getSession().disconnect();
			sftp.quit(); 
			session.disconnect();
			channel.disconnect();
		}
		return flag;
	}
	
	//sftp批量下载,根据账期
	public static boolean multiDownloadFile(String ip, String name, String password, String remoteDir, String srcDir,String month)
			throws JSchException {
		boolean flag = false;

		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();

		session = jsch.getSession(name, ip, 22);
		session.setPassword(password);
		session.setTimeout(900000);
		// session.setClientVersion("StrictHostKeyChecking");
		Properties pro = new Properties();
		pro.put("StrictHostKeyChecking", "no");
		session.setConfig(pro);
		session.connect();

		channel = (Channel) session.openChannel("sftp");
		channel.connect(1000);
		ChannelSftp sftp = (ChannelSftp) channel;
		log.info("sftp通道创建。。。。。。");
		try {
			if (!sftp.isConnected()) {
				log.error("Failed to connect FTP server! :" + ip);
				return false;
			}
			Vector ls = sftp.ls(remoteDir); //遍历该目录
			sftp.cd(remoteDir);	
			Iterator it = ls.iterator();
			Pattern p = Pattern.compile("\\.(\\d{6})\\.");			
			Matcher m = null;				
			List<String> filenames = new ArrayList<String>();
			while(it.hasNext()){
				Object next = it.next();//得到下一个元素
				String filename =next.toString();
				m = p.matcher(filename);				
				// 筛选该账期的文件
				if (!m.find() || !month.equals(m.group(1))) {				
					continue;
				}				
				if (!filename.endsWith(".DAT")) {
					continue;
				}
				filenames.add(filename);
			}
			
			log.info("filenames="+filenames.size());
			for (String filename : filenames){
				log.info("filename="+filename);
				File file= new File(srcDir+filename);
				FileOutputStream os = new FileOutputStream(file);
				sftp.get(remoteDir,os);
			}			
			
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("download error!");
		} finally {
			sftp.getSession().disconnect();
			sftp.quit();
			session.disconnect();
			channel.disconnect();
		}
		return flag;
	}
	
	/**
	  * 文件解压fileName含有后缀的文件名
	  * file_name 不带后缀的文件名
	  * @param month
	  * @param fileName
	  * @param filepath
	  * @return
	  */
	public static boolean unGzipFile(String pathlocal,String fileName){
		long startTime=System.currentTimeMillis(); 
		boolean flag = false;
	 
		String path = pathlocal + fileName;		
		File file = new File(path); 
	
//		if (!getExtension(fileName).equalsIgnoreCase("gz")) {    
//           System.err.println("File name must have extension of \".gz\"");    
//           System.exit(1);    
//       } 
		
		try {							  
			ZipInputStream zin = new ZipInputStream(new FileInputStream(file)); //解压文件流
			BufferedInputStream bin =new BufferedInputStream(zin, 8192);	//缓冲输出流
			
			ZipEntry zip;
			File fout =null;
			try {
				while((zip = zin.getNextEntry()) !=null && !zip.isDirectory()){

					fout = new File(pathlocal+zip.getName());	
					//判断解压后文件目录是否存在,不存在则新建目录			
					if(!fout.getParentFile().exists() && !fout.exists()){
						//(new File(fout.getParent())).mkdirs();
						file.getParentFile().mkdirs();
					}
					
					log.info("输出文件路径:"+fout.getPath());
					//if(fout.getName().endsWith(".html")){
					//map.put("fileName", fout.getName());
					//}
					
					//字符输出流
					BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(fout));
					
					int b;
					byte[] buf = new byte[1024];
					while((b=bin.read(buf)) !=-1){						
						bout.write(buf,0,b);					
					}
					
					bout.close();					
					log.info(fout.getName()+"解压成功");
				}
					bin.close();
					zin.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				flag = true;
			}catch(FileNotFoundException e){
				e.printStackTrace();
			} 
		 long endTime=System.currentTimeMillis();
		 log.info("耗费时间： "+(endTime-startTime)+" ms"); 		 
		 return flag;   		
	}
	
	/**
	 * 文件下载
	 * @param response
	 * @param file_name
	 * @throws Exception
	 */
	public void downLoanFile(HttpServletResponse response,String path)throws Exception{
		
		File file = new File(getHttpURLPath(path));
		String file_name = file.getName();
		log.info("输出下载文件:"+getHttpURLPath(path));
		//String path = Config.TEMP_PATH +Config.getMonth(-1)+File.separator+ file_name;
						
		try {
			
			//以流的形式下载
			InputStream bis = new BufferedInputStream(new FileInputStream(path)); 
			
			byte[] buf = new byte[bis.available()];
			bis.read(buf);			
			bis.close();
			response.reset();  //清空response

			response.addHeader("Content-Disposition", "attachment;filename="+file_name);
			response.addHeader("Content-Length", "" + file.length());
			OutputStream out = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			out.write(buf);
			out.flush();
			out.close();
			 			 
		}catch (IOException e) {
			e.printStackTrace();	
		}
	}
		
	public static String getExtension(String f) {    
       String ext = "";    
       int i = f.lastIndexOf('.');     
       if (i > 0 &&  i < f.length() - 1) {    
           ext = f.substring(i+1);    
       }         
       return ext;    
   } 
	
 public static String getHttpURLPath(String path) {  
        return path.replace("\\", "/");  
    } 
	
}
