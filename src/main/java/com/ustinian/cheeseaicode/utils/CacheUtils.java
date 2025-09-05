package com.ustinian.cheeseaicode.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 缓存工具类
 *
 * @author Ustinian-zh
 */
@Component
@Slf4j
public class CacheUtils {

    @Resource
    private CacheManager cacheManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 清理指定缓存
     *
     * @param cacheName 缓存名称
     */
    public void clearCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("成功清理缓存: {}", cacheName);
            } else {
                log.warn("缓存不存在: {}", cacheName);
            }
        } catch (Exception e) {
            log.error("清理缓存失败: {}, 错误: {}", cacheName, e.getMessage());
        }
    }

    /**
     * 强力清理指定缓存（直接操作Redis，清理所有匹配的key）
     */
    public void forceClearCache(String cacheName) {
        try {
            // 先尝试Spring Cache的清理
            clearCache(cacheName);
            
            // 再直接操作Redis清理所有匹配的key
            String pattern = cacheName + "::*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("强力清理缓存成功: {} (清理了 {} 个key)", cacheName, keys.size());
            } else {
                log.info("没有找到匹配的缓存key: {}", pattern);
            }
        } catch (Exception e) {
            log.error("强力清理缓存失败: {}, 错误: {}", cacheName, e.getMessage());
        }
    }

    /**
     * 清理所有缓存
     */
    public void clearAllCaches() {
        try {
            cacheManager.getCacheNames().forEach(this::clearCache);
            log.info("成功清理所有缓存");
        } catch (Exception e) {
            log.error("清理所有缓存失败: {}", e.getMessage());
        }
    }
}