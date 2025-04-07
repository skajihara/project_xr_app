package com.skajihara.project_xr_app.infrastructure.controller;

import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.infrastructure.controller.model.ScheduledTweetModel;
import com.skajihara.project_xr_app.infrastructure.service.TweetScheduleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/schedule")
public class TweetScheduleController {

    @Autowired
    private TweetScheduleService tweetScheduleService;

    /**
     * 1件の予約ツイート情報を取得する
     *
     * @param scheduleId 予約ツイートID
     * @return １件のツイート情報
     */
    @GetMapping("/{scheduleId}")
    public ScheduledTweetModel getScheduledTweet(@Positive @PathVariable int scheduleId) {
        return convertToResponseModel(tweetScheduleService.getScheduledTweet(scheduleId));
    }

    /**
     * 特定アカウントの予約ツイート情報を取得する
     *
     * @param accountId アカウントID
     * @return 特定アカウントの指定件数の予約ツイート情報
     */
    @GetMapping("/account/{accountId}")
    public List<ScheduledTweetModel> getScheduledTweetsByAccountId(@PathVariable String accountId) {
        return convertToResponseModelList(tweetScheduleService.getScheduledTweetsByAccountId(accountId));
    }

    /**
     * 1件のツイートを予約する
     *
     * @param schedule 予約ツイート情報
     * @return 予約結果
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> scheduleTweet(@Valid @NotNull @RequestBody ScheduledTweetModel schedule) {

        ScheduledTweetRecord tweetSchedule = convertToScheduledTweetRecord(schedule);
        int result = tweetScheduleService.scheduleTweet(tweetSchedule);
        Map<String, String> response = new HashMap<>();

        if (result == 0) {
            response.put("message", "ツイート予約失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "ツイート予約成功");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 1件の予約ツイートを更新する
     *
     * @param scheduleId      更新対象予約ツイートID
     * @param updatedSchedule 予約ツイート更新情報
     * @return 更新結果
     */
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Map<String, String>> updateSchedule(@Positive @PathVariable int scheduleId, @Valid @NotNull @RequestBody ScheduledTweetModel updatedSchedule) {

        ScheduledTweetRecord tweetSchedule = convertToScheduledTweetRecord(updatedSchedule);
        int result = tweetScheduleService.updateSchedule(scheduleId, tweetSchedule);
        Map<String, String> response = new HashMap<>();

        if (result == 0) {
            response.put("message", "予約更新失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "予約更新成功");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 1件のツイートをキャンセルする
     *
     * @param scheduleId キャンセル対象予約ツイートID
     * @return キャンセル結果
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Map<String, String>> cancelSchedule(@Positive @PathVariable int scheduleId) {

        int result = tweetScheduleService.cancelSchedule(scheduleId);

        Map<String, String> response = new HashMap<>();
        if (result == 0) {
            response.put("message", "予約キャンセル失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "予約キャンセル成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private ScheduledTweetModel convertToResponseModel(ScheduledTweetRecord serviceResult) {
        if (serviceResult == null) {
            return new ScheduledTweetModel();
        }
        ScheduledTweetModel responseResult = new ScheduledTweetModel();
        responseResult.setId(serviceResult.getId());
        responseResult.setAccountId(serviceResult.getAccountId());
        responseResult.setText(serviceResult.getText());
        responseResult.setImage(serviceResult.getImage());
        responseResult.setLocation(serviceResult.getLocation());
        responseResult.setScheduledDatetime(serviceResult.getScheduledDatetime());
        responseResult.setCreatedDatetime(serviceResult.getCreatedDatetime());
        responseResult.setDeleteFlag(serviceResult.getDeleteFlag());
        return responseResult;
    }

    private List<ScheduledTweetModel> convertToResponseModelList(List<ScheduledTweetRecord> serviceResults) {
        return serviceResults.stream()
                .map(this::convertToResponseModel)
                .collect(Collectors.toList());
    }

    private ScheduledTweetRecord convertToScheduledTweetRecord(ScheduledTweetModel model) {
        ScheduledTweetRecord tweet = new ScheduledTweetRecord();
        tweet.setId(model.getId());
        tweet.setAccountId(model.getAccountId());
        tweet.setText(model.getText());
        tweet.setImage(model.getImage());
        tweet.setLocation(model.getLocation());
        tweet.setScheduledDatetime(model.getScheduledDatetime());
        tweet.setCreatedDatetime(model.getCreatedDatetime());
        tweet.setDeleteFlag(model.getDeleteFlag());
        return tweet;
    }
}
