package com.loushuiyifan.system.service;

import com.loushuiyifan.config.redis.RedisDAO;
import com.loushuiyifan.system.vo.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 漏水亦凡
 * @create 2017-05-22 16:53.
 */
@Service
public class RedisService {

    @Autowired
    RedisDAO redisDAO;

    public List<RedisKey> list(String key) {
        List<RedisKey> list;
        if (StringUtils.isEmpty(key)) {
            list = redisDAO.scanKeys();
        } else {
            key = String.format("*%s*", key);
            list = redisDAO.keys(key);
        }

        return list;
    }

    public RedisKey info(String key) {
        return redisDAO.info(key);
    }

    public int delete(String[] keys) {
        if (keys == null) {
            return 0;
        }
        Set<String> set = new HashSet<>();
        for (String key : keys) {
            set.add(key);
        }
        redisDAO.del(set);
        return keys.length;
    }


}
