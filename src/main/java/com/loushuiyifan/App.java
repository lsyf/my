package com.loushuiyifan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class App extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {
    protected final static Logger logger = LoggerFactory.getLogger(App.class);

    //生成war包的配置
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }


    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
//        container.setPort(8080);
    }


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        logger.info("App is success!");
    }
}
