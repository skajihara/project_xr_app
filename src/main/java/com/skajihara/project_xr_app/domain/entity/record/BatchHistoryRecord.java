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

    @Column(name = "execution_start", nullable = false)
    private Date executionStart = new Date();

    @Column(name = "execution_end")
    private Date executionEnd;

    @Column(nullable = false)
    private int succeeded;
}
