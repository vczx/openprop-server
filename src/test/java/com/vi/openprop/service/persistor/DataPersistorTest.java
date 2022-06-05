package com.vi.openprop.service.persistor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.openprop.config.URAConfig;
import com.vi.openprop.dto.ProjectDto;
import com.vi.openprop.dto.URAResponseDto;
import com.vi.openprop.entity.Project;
import com.vi.openprop.entity.Transaction;
import com.vi.openprop.repository.BatchAuditRepository;
import com.vi.openprop.repository.ProjectRepository;
import com.vi.openprop.service.fetcher.InternalDataFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPersistorTest {

    @Mock
    URAConfig uraConfig;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    BatchAuditRepository batchAuditRepository;

    @Mock
    InternalDataFetcher internalDataFetcher;

    @Mock
    private CacheManager cacheManager;

    @TempDir
    public File tempDir;

    final String sampleResponse = "src/test/resources/sampleResponse.json";
    String response = "";

    @BeforeEach
    public void initTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        response = Files.readString(Path.of(sampleResponse));
    }

    @Test
    public void test_persist_response_file() throws IOException {
        given(this.uraConfig.getUraResponseSavePath()).willReturn(tempDir.getAbsolutePath());
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository, internalDataFetcher, cacheManager);
        Path writePath = dataPersistor.persistResponseAsFile("some random content", 0);
        assertTrue(Files.exists(writePath));
    }

    @Test
    public void test_parse_ura_resopnse() throws IOException {
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository, internalDataFetcher, cacheManager);
        String response = Files.readString(Path.of(sampleResponse));
        List<ProjectDto> projectDtoList = dataPersistor.parseUraResponse(response);
        assertEquals(1, projectDtoList.size());

        List<Project> projects = ProjectDto.convertDtoToEntity(projectDtoList);
        assertEquals(1, projects.size());

        Project project = projects.get(0);
        assertNotNull(project.getId());
        assertEquals("LANDED HOUSING DEVELOPMENT", project.getProject());
        assertNotNull(project.getCreateDate());

        assertEquals(1, project.getTransactions().size());
        Transaction t = project.getTransactions().get(0);

        assertEquals(524.3d, t.getArea());
        assertNotNull(t.getCreateDate());
    }

    @Test
    public void test_full_data_load() throws IOException {
        final String fullBatch1 = "src/test/resources/PMI_Resi_Transaction_20211103.json";
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository, internalDataFetcher, cacheManager);
        String response = Files.readString(Path.of(fullBatch1));
        dataPersistor.persistUraDataToDB(response, 1);
    }

    @Test
        //TODO: enhance with meaningful test cases
    void test_recon_with_cache() {
        DataPersistor dataPersistor = new DataPersistor(uraConfig, projectRepository, batchAuditRepository, internalDataFetcher, cacheManager);

        final String newProjectsPath = "src/test/resources/reconTest/six_projects.dat";
        List<Project> oldProjects = loadTestProjects(newProjectsPath);

        Project artra = oldProjects.stream().filter(project -> project.getProject().equals("ARTRA")).findFirst().get();
        artra.setTransactions(artra.getTransactions().stream().limit(11).collect(Collectors.toList()));

        oldProjects.remove(oldProjects.size() - 1);

        List<Project> newProjects = loadTestProjects(newProjectsPath);
        assertEquals(5, oldProjects.size());
        assertEquals(6, newProjects.size());
        var transId = oldProjects.stream().map(Project::getTransactions).flatMap(transactions -> transactions.stream().map(Transaction::getId)).collect(Collectors.toSet());
        when(internalDataFetcher.getAllTransactionIds()).thenReturn(transId);
        var projectEntities = dataPersistor.reconTransactionDataCache(newProjects);
        assertEquals(2, projectEntities.size());
        assertEquals(400, projectEntities.get(0).getTransactions().size());
        assertEquals(2, projectEntities.get(1).getTransactions().size());
    }

    private List<Project> loadTestProjects(final String path) {
        try {


            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Read objects
            List<ProjectDto> oldProjects = (List<ProjectDto>) oi.readObject();

            oi.close();
            fi.close();

            return ProjectDto.convertDtoToEntity(oldProjects);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void load() {
        try {
            FileOutputStream f = null;
            f = new FileOutputStream("src/test/resources/six_projects.dat");

            ObjectOutputStream o = new ObjectOutputStream(f);

            final String fullBatch1 = "src/test/resources/PMI_Resi_Transaction_20211103.json";
            String response = Files.readString(Path.of(fullBatch1));
            URAResponseDto uraResponseDto = new ObjectMapper().readValue(response.trim(), URAResponseDto.class);
            List<ProjectDto> temp = uraResponseDto.getResult();
            List<ProjectDto> temp2 = temp.stream().limit(6).collect(Collectors.toList());

            o.writeObject(temp2);
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}