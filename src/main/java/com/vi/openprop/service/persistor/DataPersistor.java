package com.vi.openprop.service.persistor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.openprop.config.URAConfig;
import com.vi.openprop.dto.ProjectDto;
import com.vi.openprop.dto.URAResponseDto;
import com.vi.openprop.entity.BatchAudit;
import com.vi.openprop.entity.Project;
import com.vi.openprop.repository.BatchAuditRepository;
import com.vi.openprop.repository.ProjectRepository;
import com.vi.openprop.service.fetcher.InternalDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vi.openprop.helpers.GlobalConstants.yyyyMMdd;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 *  DataPersistor writes to cache, database and also store the raw json response from URA to disk
 */
@Component
public class DataPersistor {
    private Logger logger = LoggerFactory.getLogger(DataPersistor.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String RESPONSE_FILE_PREFIX = "PMI_Resi_Transaction_";
    private final String RESPONSE_FILE_FORMAT = ".json";

    private URAConfig uraConfig;
    private ProjectRepository projectRepository;
    private BatchAuditRepository batchAuditRepository;
    private InternalDataFetcher internalDataFetcher;
    private CacheManager cacheManager;

    @Autowired
    public DataPersistor(URAConfig uraConfig, ProjectRepository projectRepository,BatchAuditRepository batchAuditRepository,InternalDataFetcher internalDataFetcher,CacheManager cacheManager) {
        this.uraConfig = uraConfig;
        this.projectRepository = projectRepository;
        this.batchAuditRepository = batchAuditRepository;
        this.internalDataFetcher = internalDataFetcher;
        this.cacheManager = cacheManager;
    }

    public boolean persistUraDataToDB(String uraResponse, Integer batchNum) throws IOException {
        List<ProjectDto> projectDtos = parseUraResponse(uraResponse);
        List<Project> projectEntities = ProjectDto.convertDtoToEntity(projectDtos);
        projectEntities = reconTransactionDataCache(projectEntities);
//        saveToCache(projectEntities);
        List<Project> savedProjects = projectRepository.saveAll(projectEntities);
        logger.info("Saved completed. Number of projects injected {} for batch {}", savedProjects.size(), batchNum);
        return true;
    }

    public void saveToCache(List<Project> projects){
        var cache = cacheManager.getCache("transactionCache");
        var tempt = cache.get(projects.get(0).getId());
//        if(tempt != null) tempt.get();
//        projects.forEach(project -> cache.putIfAbsent(project.getId(),project.getId()));
        logger.info("saved new transaction to cache");
    }

    List<Project> reconTransactionDataCache(List<Project> projectEntitiesBefore) {
        Set<String> transactionIds =  internalDataFetcher.getAllTransactionIds();

        return projectEntitiesBefore.stream()
                .peek(project -> project.setTransactions(project.getTransactions().stream().filter(transaction -> !transactionIds.contains(transaction.getId())).collect(Collectors.toList())))
                .filter(project -> project.getTransactions().size() != 0)
                .collect(Collectors.toList());
    }

    public Path persistResponseAsFile(String uraResponse, int batchNumber) throws IOException {
        logger.info("Persisting batch {} to file", batchNumber);
        final String dateStr = LocalDate.now().format(yyyyMMdd);
        Path dirPath = Files.createDirectories(Path.of(uraConfig.getUraResponseSavePath() + File.separator + dateStr));
        String fileName = RESPONSE_FILE_PREFIX + dateStr + "_" + batchNumber + RESPONSE_FILE_FORMAT;
        Path uraResponseFilePath = Path.of(dirPath.toString(), fileName);
        Files.deleteIfExists(uraResponseFilePath);
        return Files.write(uraResponseFilePath, uraResponse.getBytes(StandardCharsets.UTF_8), CREATE_NEW);
    }


    public void persistAuditData(final BatchAudit batchAudit){
        batchAuditRepository.save(batchAudit);
        logger.info("Saved audit info to db");
    }

    List<ProjectDto> parseUraResponse(String uraResponse) throws JsonProcessingException {
        URAResponseDto uraResponseDto = objectMapper.readValue(uraResponse.trim(), URAResponseDto.class);
        logger.info("First few json data {}", uraResponse.substring(0, 50));
        return uraResponseDto.getResult();
    }

}
