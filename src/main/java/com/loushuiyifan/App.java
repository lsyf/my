package com.loushuiyifan;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"com.loushuiyifan.common.mapper",
        "com.loushuiyifan.**.dao"})
@EnableTransactionManagement
public class App extends SpringBootServletInitializer {
    protected final static Logger logger = LoggerFactory.getLogger(App.class);

    //生成war包的配置，将项目的启动类RestjpademoApplication.java继承SpringBootServletInitializer并重写configure方法
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(this.getClass());
//    }


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        logger.info("App is success!");
    }
}
