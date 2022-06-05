package com.vi.openprop.entity;

import com.vi.openprop.dto.ProjectDto;
import com.vi.openprop.helpers.IdGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {
    //TODO: handle "LANDED HOUSING DEVELOPMENT" as project. Duplicate could occur in same street
    @Id
    private String id;
    private String project;
    private String marketSegment;
    private String street;
    @Column(name = "x")
    private String xCoordinate;
    @Column(name = "y")
    private String yCoordinate;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
    private Date updateDate;
    private Date createDate;

    public Project(ProjectDto p, List<Transaction> transactions) {
        this.project = p.getProject();
        this.marketSegment = p.getMarketSegment();
        this.street = p.getStreet();
        this.xCoordinate = p.getX();
        this.yCoordinate = p.getY();
        this.transactions = transactions;
        this.updateDate = new Date();
        this.createDate = new Date();
        this.id = IdGenerator.generateId(this.toString()).orElseGet(() -> String.valueOf(hashCode()));
    }

    @Override
    public String toString() {
        return "Project{" +
                "project='" + project + '\'' +
                ", marketSegment='" + marketSegment + '\'' +
                ", street='" + street + '\'' +
                ", xCoordinate='" + xCoordinate + '\'' +
                ", yCoordinate='" + yCoordinate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project1 = (Project) o;
        return Objects.equals(project, project1.project) && Objects.equals(marketSegment, project1.marketSegment) && Objects.equals(street, project1.street) && Objects.equals(xCoordinate, project1.xCoordinate) && Objects.equals(yCoordinate, project1.yCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, marketSegment, street, xCoordinate, yCoordinate);
    }
}
