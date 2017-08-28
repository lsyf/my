package com.loushuiyifan.mybatis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 漏水亦凡
 * @date 2017/8/2
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ImportResource({"classpath:spring-tx.xml"})
public class TXConfig {

}
