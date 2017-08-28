package com.loushuiyifan.web;

import com.loushuiyifan.App;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-03-17 10:16.
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {App.class})
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.valueOf("application/json;charset=UTF-8"));
        configurer.mediaType("xml", MediaType.valueOf("application/xml;charset=UTF-8"));
        configurer.mediaType("html", MediaType.valueOf("text/html;charset=UTF-8"));
        configurer.mediaType("*", MediaType.valueOf("*/*;charset=UTF-8"));
    }

    /**
     * 整合fastjson
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(new MediaType("text", "plain", Charset.forName("UTF-8")));
        list.add(new MediaType("*", "*", Charset.forName("UTF-8")));
        stringConverter.setSupportedMediaTypes(list);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> jsonList = new ArrayList<MediaType>();
        jsonList.add(MediaType.valueOf("application/json;charset=UTF-8"));
        jsonList.add(MediaType.valueOf("text/plain;charset=UTF-8"));
        jsonList.add(MediaType.valueOf("text/html;charset=UTF-8"));
        jsonConverter.setSupportedMediaTypes(jsonList);

        converters.add(stringConverter);
        converters.add(jsonConverter);
    }

    // 配置文件上传
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setMaxUploadSize(10485760000L);
        multipartResolver.setMaxInMemorySize(40960);
        return multipartResolver;
    }


    /**
     * 静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //使用资源的 MD5 作为版本号，是 VersionResourceResolver 的其中一种策略(无效)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addResolver(new VersionResourceResolver()
                        .addContentVersionStrategy("/**"));

        registry.addResourceHandler("/pic/**")
                .addResourceLocations("file:D:\\pic\\")
                .setCachePeriod(3155926);

    }


    /**
     * 静态资源处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }



    /**
     * Thymeleaf 配置
     *
     * @return
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        //开发时 禁止缓存
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addTemplateResolver(templateResolver());
        templateEngine.addTemplateResolver(new UrlTemplateResolver());
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }




}
