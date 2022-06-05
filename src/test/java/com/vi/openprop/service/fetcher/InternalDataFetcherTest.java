package com.vi.openprop.service.fetcher;

import com.vi.openprop.config.URAConfig;
import com.vi.openprop.repository.BatchAuditRepository;
import com.vi.openprop.repository.ProjectRepository;
import com.vi.openprop.service.persistor.DataPersistor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class InternalDataFetcherTest {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BatchAuditRepository batchAuditRepository;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private InternalDataFetcher internalDataFetcher;

    @Mock
    private URAConfig uraConfig;

    @Test
    public void test_load_all_transaction_ids() throws IOException {
        given(uraConfig.isPersistUraResponse()).willReturn(false);
        final String fullBatch1 = "src/test/resources/PMI_Resi_Transaction_20211103.json";
        String response = Files.readString(Path.of(fullBatch1));
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository,internalDataFetcher,cacheManager);
        dataPersistor.persistUraDataToDB(response, 1);

        var result = projectRepository.findAllProjectIds();
        assertNotNull(result);
        assertTrue(result.size() > 0);

        dataPersistor.persistUraDataToDB(response, 1);
    }
}