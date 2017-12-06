package com.loushuiyifan.config.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Collection;
import java.util.Set;

/**
 * @author 漏水亦凡
 * @date 2017/7/14
 */
public class ShiroRedisCacheManager implements CacheManager {

    public static final Logger log = LoggerFactory.getLogger(ShiroRedisCacheManager.class);
    private static final String PRE = "shiro_redis_cache:";

    private org.springframework.cache.CacheManager springCacheManger;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        log.debug("getCache: {} ", name);
        org.springframework.cache.Cache springCache = springCacheManger.getCache(name);
        return new SpringCacheWrapper<>(springCache, name);
    }

    public void setSpringCacheManger(org.springframework.cache.CacheManager springCacheManger) {
        this.springCacheManger = springCacheManger;
    }

    private class SpringCacheWrapper<K, V> implements Cache<K, V> {
        private org.springframework.cache.Cache springCache;
        private String name;

        public SpringCacheWrapper(org.springframework.cache.Cache springCache, String name) {
            this.springCache = springCache;
            this.name = PRE + name;
        }

        @Override
        public V get(K key) throws CacheException {
            String temp = name + key;
            log.debug("get-> Key: {}", temp);

            Object value = springCache.get(temp);

            if (value instanceof SimpleValueWrapper) {
                return (V) ((SimpleValueWrapper) value).get();
            }
            return (V) value;
        }

        @Override
        public V put(K key, V value) throws CacheException {
            String temp = name + key;
            log.debug("put-> Key: {}, value: {}", temp, value);


            springCache.put(temp, value);
            return value;
        }

        @Override
        public V remove(K key) throws CacheException {
            String temp = name + key;
            log.debug("remove-> Key: {}", temp);


            //Object value = get(key);
            springCache.evict(temp);
            return null;
        }

        @Override
        public void clear() throws CacheException {
            springCache.clear();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("invoke spring cache abstract size method not supported");

        }

        @Override
        public Set<K> keys() {
            throw new UnsupportedOperationException("invoke spring cache abstract keys method not supported");

        }

        @Override
        public Collection<V> values() {
            throw new UnsupportedOperationException("invoke spring cache abstract values method not supported");
        }
    }
}
