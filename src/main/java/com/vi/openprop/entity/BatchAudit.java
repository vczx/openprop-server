package com.vi.openprop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "BatchAudit")
@Getter
@Setter
@NoArgsConstructor
public class BatchAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "batch_date")
    private String batchDate;

    @Column(name = "complete_status")
    private String completeStatus;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "mode")
    private String mode;

}
