package com.loushuiyifan.system.service;

import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author 漏水亦凡
 * @date 2017/12/7
 */
@Service
public class SendShortMsgService {
    Logger logger = LoggerFactory.getLogger(SendShortMsgService.class);

    @Autowired(required = false)
    UCCPSendService uccpSendService;


    public String send(String phone) throws Exception {
        HashMap params = new HashMap();
        String systemCode = "INCSYS";
        String password = "a12345";
        String seq = (new Random().nextInt(1000000000) + 1000000000) + "";
        String code = (new Random().nextInt(9000) + 999) + "";
        String content = String.format("[收入系统登录]您的短信口令为%s，有效期3分钟", code);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        DateTimeFormatter yyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");


        //请求消息流水，格式：系统编码（6位）+yyyymmddhhmiss+10位序列号
        params.put("TransactionId", systemCode + now.format(yyyyMMdd) + seq);
        //UCCP分配的系统编码
        params.put("SystemCode", systemCode);
        //UCCP分配的认证密码
        params.put("Password", password);
        //UCCP分配的帐号
        params.put("UserAcct", systemCode);
        //请求的时间,请求发起的时间,必须为下边的格式
        params.put("RequestTime", now.format(yyyy_MM_dd));
        //接收消息推送的手机号码
        params.put("AccNbr", phone);
        //消息内容
        params.put("OrderContent", content);
        //场景标识
        params.put("SceneId", "5640");
        //本地网/辖区
        params.put("LanId", "571");
        //定时发送的时间设置
        params.put("SendDate", "");
        //如果使用场景模板来发送短信,必须填值
        params.put("ContentParam", "");
        //外系统流水ID,查询发送结构用,可填
        params.put("ExtOrderId", "");


        logger.info("接口参数:" + params);
        Map reqMap = uccpSendService.sendShortMessage(params);
        logger.info("接口返回结果:" + reqMap);
        return code;
    }
}
