package com.loushuiyifan.redis.bean;

/**
 * @author 漏水亦凡
 * @create 2017-03-07 11:25.
 */
public class RedisKey {
    private String key;
    private String type;
    private long time;
    private long length;

    public RedisKey() {
    }

    public RedisKey(String key) {
        this.key = key;
    }

    public RedisKey(byte[] key) {
        this.key = new String(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
