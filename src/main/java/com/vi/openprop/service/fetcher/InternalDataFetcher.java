package com.vi.openprop.service.fetcher;

import com.vi.openprop.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InternalDataFetcher {

    private ProjectRepository projectRepository;

    @Autowired
    public InternalDataFetcher(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

//    @Cacheable(cacheNames = "transactionCache", cacheManager = "cacheManager")
    public Set<String> getAllTransactionIds(){
        return projectRepository.findAllTransactionsIds();
    }

    @Cacheable(cacheNames = "projectCache", cacheManager = "cacheManager")
    public Set<String> getAllProjectIds(){
        return projectRepository.findAllProjectIds();
    }

}
