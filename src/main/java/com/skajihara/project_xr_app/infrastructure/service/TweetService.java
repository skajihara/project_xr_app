package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import com.skajihara.project_xr_app.exception.TweetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    /**
     * 対象1件のツイート情報を取得する
     *
     * @param tweetId 取得対象件数
     * @return １件のツイート情報
     */
    public TweetRecord getTweet(int tweetId) {
        TweetRecord tweet = tweetRepository.selectTweet(tweetId);
        if (Objects.isNull(tweet)) {
            throw new TweetException("Tweet is not found with id: " + tweetId);
        }
        return tweet;
    }

    /**
     * 全てのツイート情報を取得する
     *
     * @return 全ツイート情報
     */
    public List<TweetRecord> getAllTweets() {
        return tweetRepository.selectAllTweets();
    }

    /**
     * 最新指定件数のツイート情報を取得する
     *
     * @param limit 取得対象件数
     * @return 最新指定件数ツイート情報
     */
    public List<TweetRecord> getRecentTweets(int limit) {
        return tweetRepository.selectRecentTweets(limit);
    }

    /**
     * 特定アカウントの最新指定件数のツイートを取得する
     *
     * @param accountId アカウントID
     * @param limit     取得対象件数
     * @return 特定アカウントの指定件数ツイート情報
     */
    public List<TweetRecord> getTweetsByAccountId(String accountId, int limit) {
        return tweetRepository.selectRecentTweetsByAccountId(accountId, limit);
    }

    /**
     * 1件のツイート情報を投稿する
     *
     * @param tweet 投稿ツイート情報
     * @return 投稿結果
     */
    public int postTweet(TweetRecord tweet) {
        int result = tweetRepository.insert(tweet);
        if (result == 0) {
            throw new TweetException("Tweet Register Failed.");
        }
        return result;
    }

    /**
     * 1件のツイート情報を更新する
     *
     * @param tweetId 更新対象ツイートID
     * @param tweet   　更新ツイート情報
     * @return 更新結果
     */
    public int updateTweet(int tweetId, TweetRecord tweet) {
        TweetRecord target = tweetRepository.selectTweet(tweetId);
        if (Objects.isNull(target)) {
            throw new TweetException("Tweet is not found with id: " + tweetId);
        }
        int result = tweetRepository.update(target.getId(), tweet);
        if (result == 0) {
            throw new TweetException("Tweet Update Failed.");
        }
        return result;
    }

    /**
     * 1件のツイート情報を削除する
     *
     * @param tweetId 削除対象ツイートID
     * @return 削除結果
     */
    public int deleteTweet(int tweetId) {
        TweetRecord target = tweetRepository.selectTweet(tweetId);
        if (Objects.isNull(target)) {
            throw new TweetException("Tweet is not found with id: " + tweetId);
        }
        int result = tweetRepository.delete(tweetId);
        if (result == 0) {
            throw new TweetException("Tweet Delete Failed.");
        }
        return result;
    }
}
