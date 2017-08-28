package com.loushuiyifan.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

/**
 * @author 漏水亦凡
 * @create 2017-05-22 16:36.
 */

@Configuration
@PropertySource({"classpath:redis.properties"})
@EnableCaching(proxyTargetClass = true)
public class RedisConfig extends JCacheConfigurerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private Environment env;

    private static String _T = "_";

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager(stringRedisTemplate());
//        cacheManager.setDefaultExpiration(30 * 60); // 30min
        cacheManager.setDefaultExpiration(30 * 24 * 60 * 60); // 30d

        return cacheManager;
    }


    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(env.getRequiredProperty("redis.config.maxTotal", Integer.class));
        config.setMaxIdle(env.getRequiredProperty("redis.config.maxIdle", Integer.class));
        config.setMinIdle(env.getRequiredProperty("redis.config.minIdle", Integer.class));
        config.setMaxWaitMillis(env.getRequiredProperty("redis.config.maxWaitMillis", Long.class));
        config.setMinEvictableIdleTimeMillis(env.getRequiredProperty("redis.config.minEvictableIdleTimeMillis", Long.class));
        config.setNumTestsPerEvictionRun(env.getRequiredProperty("redis.config.numTestsPerEvictionRun", Integer.class));
        config.setTimeBetweenEvictionRunsMillis(env.getRequiredProperty("redis.config.timeBetweenEvictionRunsMillis", Long.class));
        config.setTestOnBorrow(env.getRequiredProperty("redis.config.testOnBorrow", Boolean.class));
        config.setTestOnReturn(env.getRequiredProperty("redis.config.testOnReturn", Boolean.class));
        config.setTestWhileIdle(env.getRequiredProperty("redis.config.testWhileIdle", Boolean.class));

        return config;
    }


    @Bean(destroyMethod = "destroy")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(jedisPoolConfig());
        factory.setHostName(env.getRequiredProperty("redis.hostName"));
        factory.setPort(env.getRequiredProperty("redis.port", Integer.class));
        factory.setTimeout(env.getRequiredProperty("redis.timeout", Integer.class));
        factory.setDatabase(env.getRequiredProperty("redis.database", Integer.class));
        factory.setPassword(env.getRequiredProperty("redis.password"));
        factory.setUsePool(env.getRequiredProperty("redis.usePool", Boolean.class));

        return factory;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public JdkSerializationRedisSerializer jdkSerializationRedisSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer());

        return redisTemplate;
    }

    @Bean("stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer());
        return redisTemplate;
    }


    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuffer sb = new StringBuffer("aweb_json:");
                sb.append(o.getClass().getName());
                sb.append(_T).append(method.getName());
                for (Object obj : objects) {
                    sb.append(_T).append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                LOG.info("cache get error: {}", key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                LOG.info("cache put error: {}", key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                LOG.info("cache evict error: {}", key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                LOG.info("cache clear error");
            }
        };
    }


}
