package com.skajihara.project_xr_app.infrastructure.controller;

import com.skajihara.project_xr_app.infrastructure.controller.model.TweetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    @Autowired
    private final TweetService tweetService;

    /**
     * 対象1件のツイート情報を取得する
     * @param tweetId 取得対象件数
     * @return １件のツイート情報
     */
    @GetMapping("/{tweetId}")
    public TweetModel getTweet(@PathVariable int tweetId) {
        return tweetService.selectTweet(tweetId);
    }

    /**
     * 全てのツイート情報を取得する
     * @return 全ツイート情報
     */
    @GetMapping("/list")
    public List<TweetModel> getAllTweets() {
        return tweetService.selectAllTweets();
    }

    /**
     * 最新N件のツイート情報を取得する
     * @param limit 取得対象件数
     * @return 全ツイート
     */
    @GetMapping("/recent")
    public List<TweetModel> getRecentTweets(@RequestParam(defaultValue = "20") int limit) {
        return tweetService.selectRecentTweets(limit);
    }

    /**
     * 特定アカウントの最新N件のツイートを取得する
     * @param accountId アカウントID
     * @param limit 取得対象件数
     * @return 全ツイート
     */
    @GetMapping("/{account_id}")
    public List<TweetModel> getTweetsByAccountId(@PathVariable String accountId, @RequestParam(defaultValue = "20") int limit) {
        return tweetService.selectTweetsByAccountId(accountId, limit);
    }

    /**
     * 1件のツイート情報を登録する
     * @param tweet ツイート情報
     * @return 登録結果
     */
    @PostMapping
    public boolean createTweet(@RequestBody TweetModel tweet) {
        tweetService.createTweet(tweet);
    }

    /**
     * 1件のツイート情報を更新する
     * @param tweetId ツイートID
     * @param tweet ツイート情報
     * @return 更新結果
     */
    @PutMapping("/{tweetId}")
    public boolean updateTweet(@PathVariable int tweetId, @RequestBody TweetModel tweet) {
        tweetService.updateTweet(tweetId, tweet);
    }

    /**
     * 1件のツイート情報を削除する
     * @param tweetId ツイートID
     * @return 削除結果
     */
    @DeleteMapping("/{tweetId}")
    public void deleteTweet(@PathVariable int tweetId) {
        tweetService.deleteTweet(tweetId);
    }
}
