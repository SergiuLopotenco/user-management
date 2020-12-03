package com.user.management.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {

    @CacheEvict(allEntries = true, value = "usersCache")
    @Scheduled(fixedDelayString = "30000")
    public void cacheEvict() {
        log.info(" ***** Cache Evict -> " + LocalDateTime.now() + " *****");
    }

}
