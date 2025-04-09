package com.skajihara.project_xr_app.domain.entity.record;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "SCHEDULED_TWEETS")
public class ScheduledTweetRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 20)
    private String accountId;

    @Column(nullable = false, length = 200)
    private String text;

    @Column(length = 100)
    private String image;

    @Column(length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDateTime scheduledDatetime;

    @Column(nullable = false)
    private LocalDateTime createdDatetime;

    @Column(nullable = false)
    private int deleteFlag;

    public ScheduledTweetRecord() {
    }
}
