package com.secqme.util.cache;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.Set;

/**
 * Created by edward on 06/02/2015.
 */
public class RedisCacheUtil implements CacheUtil {

    private static JedisPool pool = null;
//    private static JedisPool pool = null;
    
    private static final String REDIS_KEY_PREFIX = "ss_";
    private static final Logger myLog = Logger.getLogger(RedisCacheUtil.class);
    private static int SECONDS_IN_2_WEEKS = 60 * 60 * 24 * 14;
    private String hostName;
    private Integer portNumber;

    public RedisCacheUtil(String hostName,Integer portNumber) {

        this.hostName = hostName;
        this.portNumber = portNumber;
        myLog.debug("hostName " + hostName + " port " + portNumber);
        pool = new JedisPool(hostName, portNumber);
    }

    @Override
    public Object getCachedObject(String key,Class className) {
        Jedis jedis = null;
        Object result = null;
        try {
            jedis = pool.getResource();
            ObjectMapper mapper = new ObjectMapper();
            if (jedis.get(key) != null) {
                result = mapper.readValue(jedis.get(key), className);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            pool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public void storeObjectIntoCache(String key, Object value) {
        Jedis jedis = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jedis = pool.getResource();
            jedis.setex(key, SECONDS_IN_2_WEEKS, mapper.writeValueAsString(value));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void expireCachedObject(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void expireAllCacheObject() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> keys = jedis.keys(REDIS_KEY_PREFIX + "*");
            for (String key : keys) {
                jedis.del("");
                jedis.del(key.getBytes());
            }
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    private static byte[] getKeyWithPrefix(String key) {
        return (REDIS_KEY_PREFIX + key).getBytes();
    }

    @Override
    public void displayDetails() {

    }
}
