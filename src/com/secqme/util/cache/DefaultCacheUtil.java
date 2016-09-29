package com.secqme.util.cache;

import net.sf.ehcache.*;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.log4j.Logger;

/**
 * A Default Cache Util to store System Paramters that seldom changes
 * Hence the cache is set to expire every day, 24 hours 
 * @author coolboykl
 */
public class DefaultCacheUtil {
    
    private static final String SYSTEM_CACHE_NAME = "WOMCrimeDataCacheSystem";
    private static CacheManager cacheManager;
    private Cache systemCache = null;
    private static long SECONDS_IN_A_DAY = 60 * 60 * 24;
    private final static Logger myLog = Logger.getLogger(DefaultCacheUtil.class);
    
    public DefaultCacheUtil() {
        cacheManager = CacheManager.create();
        systemCache = new Cache(SYSTEM_CACHE_NAME,
                50000,
                true,
                false,
                SECONDS_IN_A_DAY * 14,
                SECONDS_IN_A_DAY * 14);
        cacheManager.addCache(systemCache);

    }

    public void displayDetails(){
        Cache cache = cacheManager.getCache(SYSTEM_CACHE_NAME);
        myLog.debug("Status : " + cache.getStatus());
        myLog.debug("Size : " + cache.getSize());
        myLog.debug("Statistics : " + cache.getStatistics());
        myLog.debug("Memory size(cal) : " + cache.calculateInMemorySize());
    }

    public Object getCachedObject(String cacheKey) {
        Object cacheObject = null;
        try {
            Cache cache = cacheManager.getCache(SYSTEM_CACHE_NAME);
            if (cache != null && cache.isElementInMemory(cacheKey) && cache.get(cacheKey) != null) {
                cacheObject = cache.get(cacheKey).getObjectValue();
            }
        } catch (CacheException ce) {
            ce.printStackTrace();
        }
        return cacheObject;
    }

    public void storeObjectIntoCache(String cacheKey, Object obj) {
        try {
            Cache cache = cacheManager.getCache(SYSTEM_CACHE_NAME);
            if (cache != null) {
                cache.put(new Element(cacheKey, obj));
                }
        } catch (CacheException ce) {
            ce.printStackTrace();
        }
    }

    public void expireCachedObject(String cacheKey) {
        myLog.debug("Expiring cache Obj, key:" + cacheKey);
            
        try {
            Cache cache = cacheManager.getCache(SYSTEM_CACHE_NAME);
            if (cache != null) {
                cache.remove(cacheKey);
            }
        } catch (CacheException ce) {
            ce.printStackTrace();
        }
    }

    public void expireAllCacheObject() {
        myLog.debug("Expiring all cacheObj");
        try {
            Cache cache = cacheManager.getCache(SYSTEM_CACHE_NAME);
            if (cache != null) {
                cache.removeAll();
            }
        } catch (CacheException ce) {
            ce.printStackTrace();
        }
    }
    
    
}
