package edu.uc.campusevent.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

class CacheConfigTest {

    @Test
    void cacheManager_createsCaffeineCacheManager() {
        CacheConfig config = new CacheConfig();
        CacheManager cacheManager = config.cacheManager();
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCache("events")).isNotNull();
    }
}
