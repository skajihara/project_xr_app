package com.skajihara.project_xr_app.domain.entity.record;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "BATCH_HISTORY")
public class BatchHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "last_processed_tweet_id", nullable = false)
    private int lastProcessedTweetId;

    @Column(name = "processed_num", nullable = false)
    private int processedNum;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "execution_start", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date executionStart = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "execution_end", columnDefinition = "TIMESTAMP")
    private Date executionEnd;

    @Column(nullable = false)
    private boolean succeeded = false;
}
