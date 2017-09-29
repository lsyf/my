package com.loushuiyifan.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据分析数据源
 * @author 漏水亦凡
 * @date 2017/9/18
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.loushuiyifan.data.dao"},
        sqlSessionFactoryRef = "secondarySqlSessionFactory")
public class DBConfig2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig2.class);

    static final String MAPPER_LOCATION = "classpath*:mapper2/**/*.xml";

    @Bean("secondaryDataSource")
    @ConfigurationProperties("spring.datasource.druid.secondary")
    public DataSource secondaryDataSource() {
        LOGGER.info("---------------------->secondaryDataSource");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSource") DataSource dataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MAPPER_LOCATION));

        org.apache.ibatis.session.Configuration c = new org.apache.ibatis.session.Configuration();
        c.setMapUnderscoreToCamelCase(true); //驼峰命名转换
        sessionFactory.setConfiguration(c);

        LOGGER.debug("---------------------->secondarySqlSessionFactory");
        return sessionFactory.getObject();
    }

}
