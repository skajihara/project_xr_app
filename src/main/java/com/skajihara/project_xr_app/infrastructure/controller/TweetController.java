package com.skajihara.project_xr_app.infrastructure.controller;

import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.infrastructure.controller.model.TweetModel;
import com.skajihara.project_xr_app.infrastructure.service.TweetService;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    /**
     * 1件のツイート情報を取得する
     *
     * @param tweetId ツイートID
     * @return １件のツイート情報
     */
    @GetMapping("/{tweetId}")
    public TweetModel getTweet(@Positive @PathVariable int tweetId) {
        return convertToResponseModel(tweetService.getTweet(tweetId));
    }

    /**
     * 全てのツイート情報を取得する
     *
     * @return 全ツイート情報
     */
    @GetMapping
    public List<TweetModel> getAllTweets() {
        return convertToResponseModelList(tweetService.getAllTweets());
    }

    /**
     * 最新指定件数のツイート情報を取得する
     *
     * @param limit 取得対象件数
     * @return 最新指定件数ツイート情報
     */
    @GetMapping("/recent")
    public List<TweetModel> getRecentTweets(@Positive @RequestParam(defaultValue = "20") int limit) {
        return convertToResponseModelList(tweetService.getRecentTweets(limit));
    }

    /**
     * 特定アカウントの最新指定件数のツイートを取得する
     *
     * @param accountId アカウントID
     * @param limit     取得対象件数
     * @return 特定アカウントの指定件数ツイート情報
     */
    @GetMapping("/account/{accountId}")
    public List<TweetModel> getTweetsByAccountId(@PathVariable String accountId,
                                                 @Positive @RequestParam(defaultValue = "20") int limit) {
        return convertToResponseModelList(tweetService.getTweetsByAccountId(accountId, limit));
    }

    /**
     * 1件のツイート情報を投稿する
     *
     * @param postTweet 投稿ツイート情報
     * @return 投稿結果
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> postTweet(@Valid @NotNull @RequestBody TweetModel postTweet) {

        TweetRecord tweet = convertToTweetRecord(postTweet);
        int result = tweetService.postTweet(tweet);
        Map<String, String> response = new HashMap<>();

        if (result == 0) {
            response.put("message", "ツイート投稿失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "ツイート投稿成功");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 1件のツイート情報を更新する
     *
     * @param tweetId     更新対象ツイートID
     * @param updateTweet 　更新ツイート情報
     * @return 更新結果
     */
    @PutMapping("/{tweetId}")
    public ResponseEntity<Map<String, String>> updateTweet(@Positive @PathVariable int tweetId, @Valid @NotNull @RequestBody TweetModel updateTweet) {

        TweetRecord tweet = convertToTweetRecord(updateTweet);
        int result = tweetService.updateTweet(tweetId, tweet);
        Map<String, String> response = new HashMap<>();

        if (result == 0) {
            response.put("message", "ツイート更新失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "ツイート更新成功");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 1件のツイート情報を削除する
     *
     * @param tweetId 削除対象ツイートID
     * @return 削除結果
     */
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<Map<String, String>> deleteTweet(@Positive @PathVariable int tweetId) {

        int result = tweetService.deleteTweet(tweetId);

        Map<String, String> response = new HashMap<>();
        if (result == 0) {
            response.put("message", "ツイート削除失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "ツイート削除成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private TweetModel convertToResponseModel(TweetRecord serviceResult) {
        if (serviceResult == null) {
            return new TweetModel();
        }
        TweetModel responseResult = new TweetModel();
        responseResult.setId(serviceResult.getId());
        responseResult.setAccountId(serviceResult.getAccountId());
        responseResult.setText(serviceResult.getText());
        responseResult.setImage(serviceResult.getImage());
        responseResult.setLikes(serviceResult.getLikes());
        responseResult.setRetweets(serviceResult.getRetweets());
        responseResult.setReplies(serviceResult.getReplies());
        responseResult.setViews(serviceResult.getViews());
        responseResult.setDatetime(serviceResult.getDatetime());
        responseResult.setLocation(serviceResult.getLocation());
        responseResult.setDeleteFlag(serviceResult.getDeleteFlag());
        return responseResult;
    }

    private List<TweetModel> convertToResponseModelList(List<TweetRecord> serviceResults) {
        return serviceResults.stream()
                .map(this::convertToResponseModel)
                .collect(Collectors.toList());
    }

    private TweetRecord convertToTweetRecord(TweetModel model) {
        TweetRecord tweet = new TweetRecord();
        tweet.setId(model.getId());
        tweet.setAccountId(model.getAccountId());
        tweet.setText(model.getText());
        tweet.setImage(model.getImage());
        tweet.setLikes(model.getLikes());
        tweet.setRetweets(model.getRetweets());
        tweet.setReplies(model.getReplies());
        tweet.setViews(model.getViews());
        tweet.setDatetime(model.getDatetime());
        tweet.setLocation(model.getLocation());
        tweet.setDeleteFlag(model.getDeleteFlag());
        return tweet;
    }
}
