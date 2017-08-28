package com.loushuiyifan.redis.service;

import com.loushuiyifan.common.util.ProtoStuffSerializerUtil;
import com.loushuiyifan.redis.bean.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 漏水亦凡
 * @create 2017-05-22 16:57.
 */
@Repository
public class RedisDAO {


    @Autowired
    private StringRedisTemplate template;

    /**
     * 设置过期时间
     *
     * @param key
     * @param second
     */
    public void expire(String key, long second) {
        template.expire(key, second, TimeUnit.SECONDS);
    }

    /**
     * 存放对象
     */
    public <T> Boolean setEx(String key, T t, long second) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serialize(t);
        boolean result = template.execute((RedisCallback<Boolean>) connection -> {
            connection.setEx(bkey, second, bvalue);
            return true;
        });
        return result;
    }


    /**
     * 存放列表
     */
    public <T> boolean setEx(String key, List<T> list, long second) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(list);
        boolean result = template.execute((RedisCallback<Boolean>) connection -> {
            connection.setEx(bkey, second, bvalue);
            return true;
        });
        return result;
    }

    /**
     * 当key不存在时，存放对象
     */
    public <T> boolean setNX(String key, T t) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serialize(t);
        boolean result = template.execute((RedisCallback<Boolean>) connection -> connection.setNX(bkey, bvalue));
        return result;
    }

    /**
     * 当key不存在时，存放列表
     */
    public <T> boolean setNX(String key, List<T> list) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(list);
        boolean result = template.execute((RedisCallback<Boolean>) connection -> connection.setNX(bkey, bvalue));
        return result;
    }


    /**
     * 获取对象
     */
    public <T> T get(final String key, Class<T> clz) {
        byte[] result = template.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
        if (result == null) {
            return null;
        }
        return ProtoStuffSerializerUtil.deserialize(result, clz);
    }

    /**
     * 获取列表
     */
    public <T> List<T> getList(final String key, Class<T> clz) {
        byte[] result = template.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
        if (result == null) {
            return null;
        }
        return ProtoStuffSerializerUtil.deserializeList(result, clz);
    }

    /**
     * 获取所有 key信息
     *
     * @return
     */
    public List<RedisKey> scanKeys() {
        final int size = 5;
        List<RedisKey> keys = template.execute((RedisCallback<List<RedisKey>>) connection -> {

            List<RedisKey> temp = new ArrayList<>();

            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(size).build());
            if (cursor != null) {
                while (cursor.hasNext()) {
                    byte[] key = cursor.next();
                    temp.add(new RedisKey(key));
                }
            }

            return temp;
        });
        return keys;
    }


    public List<RedisKey> keys(String pattern) {
        byte[] b = pattern.getBytes();
        return template.execute((RedisCallback<List<RedisKey>>) connection -> {
            List<RedisKey> temp = new ArrayList<>();
            Set<byte[]> keys = connection.keys(b);
            for (byte[] key : keys) {
                temp.add(new RedisKey(key));
            }
            return temp;
        });
    }

    public RedisKey info(String keyStr) {
        byte[] key = keyStr.getBytes();
        return template.execute((RedisCallback<RedisKey>) connection -> {
            long time = connection.ttl(key);
            DataType dataType = connection.type(key);
            String type = dataType.code();

            RedisKey redisKey = new RedisKey(keyStr);
            redisKey.setTime(time);
            redisKey.setType(type);
            return redisKey;
        });
    }


    /**
     * 删除key
     *
     * @param key
     */
    public void del(String key) {
        template.delete(key);
    }

    /**
     * 删除key列表
     *
     * @param keys
     */
    public void del(Set<String> keys) {
        template.delete(keys);
    }

    /**
     * 模糊删除key
     *
     * @param pattern
     */
    public void delPattern(String pattern) {
        Set<String> keys = template.keys(pattern);
        template.delete(keys);
    }

    /**
     * 清空所有key
     */
    public void clear() {
        template.getConnectionFactory().getConnection().flushDb();
    }


}
