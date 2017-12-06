package com.loushuiyifan.config.shiro.cache;

import lombok.Data;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author 漏水亦凡
 * @date 2017/12/6
 */
@Data
public class RedisSessionDao extends AbstractSessionDAO {
    private Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

    // Session超时时间，单位为毫秒
    private long expireTime = 1800001;
    private static final String KEY_PREFIX = "shiro_redis_session:";

    @Autowired
    private RedisTemplate redisTemplate;

    public RedisSessionDao() {
        super();
    }

    public RedisSessionDao(long expireTime, RedisTemplate redisTemplate) {
        super();
        this.expireTime = expireTime;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        logger.debug("===============update================");
        if (session == null || session.getId() == null) {
            return;
        }
        session.setTimeout(expireTime);
        redisTemplate.opsForValue().set(KEY_PREFIX + session.getId(), session, expireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void delete(Session session) {
        logger.debug("===============delete================");
        if (null == session) {
            return;
        }
        redisTemplate.opsForValue().getOperations().delete(KEY_PREFIX + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return redisTemplate.keys(KEY_PREFIX + "*");
    }

    @Override// 加入session
    protected Serializable doCreate(Session session) {
        logger.debug("===============doCreate================");
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);

        redisTemplate.opsForValue().set(KEY_PREFIX + session.getId(), session, expireTime, TimeUnit.MILLISECONDS);
        return sessionId;
    }

    @Override// 读取session
    protected Session doReadSession(Serializable sessionId) {
        logger.debug("==============doReadSession=================");
        if (sessionId == null) {
            return null;
        }
        return (Session) redisTemplate.opsForValue().get(KEY_PREFIX + sessionId);
    }


}
