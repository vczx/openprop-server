package com.vi.openprop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vi.openprop.entity.Project;
import com.vi.openprop.entity.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ProjectDto implements Serializable {
    private String project;
    private String marketSegment;
    private List<TransactionDto> transaction;
    private String street;
    private String y;
    private String x;

    public static List<Project> convertDtoToEntity(List<ProjectDto> projectDtos) {
        List<Project> projects = projectDtos.stream().map(p -> {
            List<Transaction> trans = p.getTransaction().stream().map(Transaction::new).collect(Collectors.toList());
            return new Project(p, trans);
        }).collect(Collectors.toList());
        projects.forEach(p -> p.getTransactions().forEach(t -> t.setProject(p)));
        return projects;
    }
}
