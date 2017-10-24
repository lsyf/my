package com.loushuiyifan.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 合并数据源,仅使用数据分析
 *
 * @author 漏水亦凡
 * @date 2017/9/18
 */
//@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {
        "com.loushuiyifan.common.mapper",
        "com.loushuiyifan.system.dao",
        "com.loushuiyifan.report.dao",
        "com.loushuiyifan.data.dao"},
        sqlSessionFactoryRef = "sqlSessionFactory")
public class DBConfig_12 {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig_12.class);

    static final String MAPPER_LOCATION = "classpath*:mapper*/**/*.xml";

    @Primary
    @Bean("dataSource")
//    @ConfigurationProperties("spring.datasource.druid.secondary")
    public DataSource dataSource() {
        LOGGER.info("---------------------->DataSource");
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MAPPER_LOCATION));

        org.apache.ibatis.session.Configuration c = new org.apache.ibatis.session.Configuration();
        c.setMapUnderscoreToCamelCase(true); //驼峰命名转换
        sessionFactory.setConfiguration(c);

        LOGGER.debug("---------------------->SqlSessionFactory");
        return sessionFactory.getObject();
    }

}
