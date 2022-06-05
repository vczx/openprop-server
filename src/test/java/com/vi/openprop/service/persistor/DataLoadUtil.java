package com.vi.openprop.service.persistor;

import com.vi.openprop.config.URAConfig;
import com.vi.openprop.entity.BatchAudit;
import com.vi.openprop.repository.BatchAuditRepository;
import com.vi.openprop.repository.ProjectRepository;
import com.vi.openprop.service.fetcher.InternalDataFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class DataLoadUtil {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BatchAuditRepository batchAuditRepository;
    @Autowired
    private InternalDataFetcher internalDataFetcher;
    @Autowired
    private CacheManager cacheManager;

    @Mock
    private URAConfig uraConfig;

    @Test
    public void test_full_data_load() throws IOException {
        given(uraConfig.isPersistUraResponse()).willReturn(false);
        final String fullBatch1 = "src/test/resources/PMI_Resi_Transaction_20211103.json";
        String response = Files.readString(Path.of(fullBatch1));
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository,internalDataFetcher, cacheManager);
        dataPersistor.persistUraDataToDB(response, 1);
//        List<ProjectDto> projectDtoList = dataPersistor.parseUraResponse(response);
//        List<Project> projects = ProjectDto.convertDtoToEntity(projectDtoList);
    }

    @Test
    public void test_audit() throws IOException {
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository,internalDataFetcher, cacheManager);
        BatchAudit batchAudit = new BatchAudit();
        batchAudit.setMode("TEST");
        batchAudit.setCreateDate(LocalDate.now());
        batchAudit.setCompleteStatus("1111");
        dataPersistor.persistAuditData(batchAudit);

        List<BatchAudit> list =  batchAuditRepository.findAll();
        assertEquals(1,list.size());
    }
}
