package com.vi.openprop.repository;

import com.vi.openprop.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query(value = "SELECT p.id from Project p")
    Set<String> findAllProjectIds();

    @Query(value = "SELECT t.id from Transaction t")
    Set<String> findAllTransactionsIds();

//    @Query(value = "SELECT p.id from Project p where p.id = :id")
//    Set<String> findProjectById(String id);
}
