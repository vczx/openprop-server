package com.vi.openprop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi.openprop.dto.LoadResultDto;
import com.vi.openprop.entity.BatchAudit;
import com.vi.openprop.service.fetcher.ExternalDataFetcher;
import com.vi.openprop.service.persistor.DataPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.vi.openprop.helpers.GlobalConstants.yyyyMMdd;

@Service
public class URAApiService {
    public static final String MODE_SCHEDULED = "SCHEDULED";
    public static final String MODE_MANUAL = "MANUAL";
    private Logger logger = LoggerFactory.getLogger(URAApiService.class);

    private ExternalDataFetcher externalDataFetcher;
    private DataPersistor dataPersistor;

    @Autowired
    public URAApiService(ExternalDataFetcher externalDataFetcher, DataPersistor dataPersistor) {
        this.externalDataFetcher = externalDataFetcher;
        this.dataPersistor = dataPersistor;
    }

    @Scheduled(cron = "0 0 1 ? * WED,SAT")
    public List<LoadResultDto> loadURAData() {
        LocalDate today = LocalDate.now();
        logger.info("Start to load URA data for date {}", today.format(yyyyMMdd));
        var responseList = loadData(List.of(1,2,3,4));
        audit(today, responseList,MODE_SCHEDULED);
        return responseList;
    }

    public List<LoadResultDto> loadBatchByNumber(Integer batchId){
        LocalDate today = LocalDate.now();
        logger.info("Start to load URA data batch {}", batchId);
        var responseList =  loadData(List.of(batchId));
        audit(today,responseList, MODE_MANUAL);
        return responseList;
    }

    private void audit(LocalDate today, List<LoadResultDto> responseList, String mode) {
        BatchAudit batchAudit = new BatchAudit();
        batchAudit.setBatchDate(today.format(yyyyMMdd));
        batchAudit.setCreateDate(today);
        StringBuilder sb = new StringBuilder();
        for (LoadResultDto d: responseList) {
            String sign = "0";
            if(d.isLoadFromUra() && d.isPersistToDb() && d.isPersistToFile()) {
                sign = "1";
            }
            sb.append(sign);
        }
        batchAudit.setCompleteStatus(sb.toString());
        batchAudit.setMode(mode);
        dataPersistor.persistAuditData(batchAudit);
    }

    private List<LoadResultDto> loadData(List<Integer> batches) {
        final List<LoadResultDto> responseDtoList = new ArrayList<>();
        try {
            int counter = 0;
            for (Integer batchNum : batches) {
                ++counter;
                var loadResponseDto = new LoadResultDto(batchNum);
                responseDtoList.add(loadResponseDto);

                final String response = externalDataFetcher.loadFromUra(batchNum);
                if(response == null) {
                    loadResponseDto.setLoadFromUra(false);
                    return responseDtoList;
                }

                logger.info("Data fetched successfully for batch {}", batchNum);

                Path savePath = dataPersistor.persistResponseAsFile(response,batchNum);
                logger.info("Persisted ura data to File for batch {} to path {}", batchNum, savePath);
                loadResponseDto.setPersistToFile(true);

                dataPersistor.persistUraDataToDB(response,batchNum);
                logger.info("Persisted ura data to DB for batch {}", batchNum);
                loadResponseDto.setPersistToDb(true);

                logger.info("Cooling Period for next call");
                if(counter < batches.size()) TimeUnit.HOURS.sleep(1);
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse the json response from ura.", e);
        } catch (IOException e) {
            logger.error("Unable to persist the json response from ura.", e);
        } catch (InterruptedException e) {
            logger.error("Sleep interrupted.", e);
        }
        return responseDtoList;
    }

}
