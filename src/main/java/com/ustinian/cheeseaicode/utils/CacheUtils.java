package com.ustinian.cheeseaicode.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

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
            }
        } catch (Exception e) {
            log.error("清理缓存失败: {}, 错误: {}", cacheName, e.getMessage());
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