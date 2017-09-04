package com.loushuiyifan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.loushuiyifan.mybatis.mapper")
@EnableTransactionManagement
public class App extends SpringBootServletInitializer {

    //生成war包的配置，将项目的启动类RestjpademoApplication.java继承SpringBootServletInitializer并重写configure方法
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(this.getClass());
//    }


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
