package com.skajihara.project_xr_app.infrastructure.controller.model;

import lombok.Data;

import java.util.Date;

@Data
public class TweetModel {
    private int id;
    private String accountId;
    private String text;
    private String image;
    private int likes = 0;
    private int retweets = 0;
    private int replies = 0;
    private int views = 0;
    private Date datetime = new Date();
    private String location;
    private boolean deleteFlag = false;
}
