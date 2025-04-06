package com.skajihara.project_xr_app.infrastructure.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TweetModel {

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

    @PositiveOrZero
    private int likes;

    @PositiveOrZero
    private int retweets;

    @PositiveOrZero
    private int replies;

    @PositiveOrZero
    private int views;

    @NotNull
    private LocalDateTime datetime;

    @Size(max = 50)
    private String location;

    @NotNull
    private int deleteFlag;

    public TweetModel() {
    }
}
