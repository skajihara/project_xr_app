package com.skajihara.project_xr_app.domain.entity.record;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "BATCH_HISTORY")
public class BatchHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "latest_processed_id", nullable = false)
    private int latestProcessedId;

    @Column(name = "processed_num", nullable = false)
    private int processedNum;

    @Column(name = "execution_start", nullable = false)
    private LocalDateTime executionStart;

    @Column(name = "execution_end")
    private LocalDateTime executionEnd;

    @Column(nullable = false)
    private int succeeded;

    public BatchHistoryRecord() {
    }
}
