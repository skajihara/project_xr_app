package com.skajihara.project_xr_app.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TweetCsvLoader {

    @Autowired
    private TweetRepository tweetRepository;

    public void loadTestTweets(String filePath) {

        tweetRepository.deleteAll();
        if (filePath.equals("")) {
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                TweetRecord tweet = new TweetRecord();
                tweet.setAccountId(line[0]);
                tweet.setText(line[1]);
                tweet.setImage(line[2]);
                tweet.setLikes(Integer.valueOf(line[3]));
                tweet.setRetweets(Integer.valueOf(line[4]));
                tweet.setReplies(Integer.valueOf(line[5]));
                tweet.setViews(Integer.valueOf(line[6]));
                tweet.setDatetime(LocalDateTime.parse(line[7]));
                tweet.setLocation(line[8]);
                tweet.setDeleteFlag(Integer.valueOf(line[9]));
                tweetRepository.save(tweet);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
