package com.loushuiyifan.shiro.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

public class ShiroRedisCacheManager2 extends AbstractCacheManager {
    private RedisTemplate<String, Object> redisTemplate;
    private static final String PRE = "edata_shiro_";


    public ShiroRedisCacheManager2(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Cache<String, Object> createCache(String name) throws CacheException {
        return new ShiroRedisCache<>(redisTemplate, name);
    }

    class ShiroRedisCache<K, V> implements Cache<K, V> {
        private Logger log = LoggerFactory.getLogger(getClass());
        private RedisTemplate<String, V> redisTemplate;
        private String prefix = "";


        public ShiroRedisCache(RedisTemplate<String, V> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        public ShiroRedisCache(RedisTemplate<String, V> redisTemplate, String prefix) {
            this(redisTemplate);
            this.prefix = PRE + prefix;
        }

        @Override
        public V get(K key) throws CacheException {
            if (log.isDebugEnabled()) {
                log.debug("get-> Key: {}", key);
            }
            if (key == null) {
                return null;
            }

            String bkey = getByteKey(key);
            V v = redisTemplate.opsForValue().get(bkey);
            return v;
        }

        @Override
        public V put(K key, V value) throws CacheException {
            if (log.isDebugEnabled()) {
                log.debug("put-> Key: {}, value: {}", key, value);
            }

            if (key == null || value == null) {
                return null;
            }

            String bkey = getByteKey(key);
            redisTemplate.opsForValue().set(bkey, value);
            return value;
        }

        @Override
        public V remove(K key) throws CacheException {
            if (log.isDebugEnabled()) {
                log.debug("remove-> Key: {}", key);
            }

            if (key == null) {
                return null;
            }

            String bkey = getByteKey(key);
//        V value = redisTemplate.opsForValue().get(bkey);
            redisTemplate.delete(bkey);
            return null;
        }

        @Override
        public void clear() throws CacheException {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
        }

        @Override
        public int size() {
            Long len = redisTemplate.getConnectionFactory().getConnection().dbSize();
            return len.intValue();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Set<K> keys() {
            String bkey = prefix + "*";
            Set<String> set = redisTemplate.keys(bkey);
            Set<K> result = Sets.newHashSet();

            if (CollectionUtils.isEmpty(set)) {
                return Collections.emptySet();
            }

            for (String key : set) {
                result.add((K) key);
            }
            return result;
        }

        @Override
        public Collection<V> values() {
            Set<K> keys = keys();
            List<V> values = new ArrayList<>(keys.size());
            for (K k : keys) {
                String bkey = getByteKey(k);
                values.add(redisTemplate.opsForValue().get(bkey));
            }
            return values;
        }

        private String getByteKey(K key) {
            if (key instanceof String) {
                String preKey = this.prefix + key;
                return preKey;
            } else {
                String json = null;
                try {
                    json = new ObjectMapper().writeValueAsString(key);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("key转换json失败", e);
                }
                return this.prefix + json;
            }
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
