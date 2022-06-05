package com.vi.openprop.helpers;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictWorker {
    @CacheEvict(value = "tokenCache", allEntries = true)
    public void evictToken() {}

    @CacheEvict(value = "transactionCache", allEntries = true)
    public void evictTransactionIds() {}

    @CacheEvict(value = "projectCache", allEntries = true)
    public void evictProjectIds() {}

    @Scheduled(cron = "0 0 23 1/1 * ?")
    public void evictTokenScheduled(){
        evictToken();
    }

    @Scheduled(cron = "0 0 12 1 1/1 ?")
    public void evictTransAndProjectCacheScheduled(){
        evictProjectIds();
        evictTransactionIds();
    }



}
