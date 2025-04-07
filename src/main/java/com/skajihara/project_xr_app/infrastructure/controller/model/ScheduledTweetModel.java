package com.skajihara.project_xr_app.infrastructure.controller.model;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "SCHEDULED_TWEETS")
public class ScheduledTweetModel {

    @PositiveOrZero
    private int id;

    @NotNull
    @Size(max = 20)
    private String accountId;

    @NotNull
    @Size(max = 200)
    private String text;

    @Size(max = 100)
    private String image;

    @Size(max = 50)
    private String location;

    @NotNull
    private LocalDateTime scheduledDatetime;

    @NotNull
    private LocalDateTime createdDatetime;

    @NotNull
    private int deleteFlag;
}
