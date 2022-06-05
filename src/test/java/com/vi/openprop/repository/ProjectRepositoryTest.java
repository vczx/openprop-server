package com.vi.openprop.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.openprop.dto.ProjectDto;
import com.vi.openprop.dto.URAResponseDto;
import com.vi.openprop.entity.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
class ProjectRepositoryTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProjectRepository projectRepository;

    final String sampleResponse = "src/test/resources/sampleResponse.json";
    final String fulleResponse = "src/test/resources/PMI_Resi_Transaction_20211103.json";

    ProjectRepositoryTest() {
    }

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(projectRepository).isNotNull();
    }

    @Test
    void test_insert() throws IOException {
        List<Project> projectDtos = createProjectsFromFile(sampleResponse);
        projectRepository.saveAll(projectDtos);

        var projectsSaved = projectRepository.findAll();
        assertEquals(1,projectsSaved.size());
//        assertEquals();
    }

//    @Test
    void test_full_insert() throws IOException {
        List<Project> projectDtos = createProjectsFromFile(fulleResponse);
        projectRepository.saveAll(projectDtos);

        var projectsSaved = projectRepository.findAll();
        assertEquals(292,projectsSaved.size());
//        assertEquals();
    }

    private List<Project> createProjectsFromFile(String filePath) throws IOException {
        String response = Files.readString(Path.of(filePath));
        var projectDtoList = new ObjectMapper().readValue(response, URAResponseDto.class).getResult();
        var projectDtos = ProjectDto.convertDtoToEntity(projectDtoList);
        return projectDtos;
    }
}