package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class TweetRepositoryTest {

    @Autowired
    TweetRepository tweetRepository;

    /**
     * ツイート1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void selectTweet_Success001() {

        // テストデータ
        int tweetId = 1;

        // テスト実行
        TweetRecord tweet = tweetRepository.selectTweet(tweetId);

        // テスト結果
        assertThat(tweet, is(notNullValue()));
        assertThat(tweet.getId(), is(1));
        assertThat(tweet.getAccountId(), is("user_A"));
        assertThat(tweet.getText(), is("富山のホタルイカ、最高🍻"));
        assertThat(tweet.getImage(), is("/src/assets/images/img02.jpg"));
        assertThat(tweet.getLikes(), is(9));
        assertThat(tweet.getRetweets(), is(23));
        assertThat(tweet.getReplies(), is(7));
        assertThat(tweet.getViews(), is(14));
        assertThat(tweet.getDatetime(), is(LocalDateTime.of(2024, 3, 1, 15, 30, 0)));
        assertThat(tweet.getLocation(), is("富山県滑川市"));
        assertThat(tweet.getDeleteFlag(), is(0));
    }

    /**
     * ツイート1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void selectTweet_Error001() {

        // テストデータ
        int tweetId = 99999;

        // テスト実行
        TweetRecord tweet = tweetRepository.selectTweet(tweetId);

        // テスト結果
        assertThat(tweet, is(nullValue()));
    }

    /**
     * ツイート全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void selectAllTweets_Success001() {

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectAllTweets();

        // テスト結果
        assertThat(tweets, is(notNullValue()));
        assertThat(tweets.size(), is(10));
        tweets.forEach(tweet -> assertThat(tweet.getDeleteFlag(), is(0)));
    }

    /**
     * ツイート全件取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void selectAllTweets_Success002() {

        // クリアデータ
        tweetRepository.deleteAll();

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectAllTweets();

        // テスト結果
        assertThat(tweets.size(), is(0));
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 5件指定 5件取得
     */
    @Test
    void selectRecentTweets_Success001() {

        // テストデータ
        int limit = 5;

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweets(limit);

        // テスト結果
        assertThat(tweets, is(notNullValue()));
        assertThat(tweets.size(), is(limit));
        tweets.forEach(tweet -> assertThat(tweet.getDeleteFlag(), is(0)));

        // datetimeの降順になっているか検証
        for (int i = 0; i < tweets.size() - 1; i++) {
            assertThat(tweets.get(i).getDatetime().isAfter(tweets.get(i + 1).getDatetime()) ||
                    tweets.get(i).getDatetime().isEqual(tweets.get(i + 1).getDatetime()), is(true));
        }
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 100件指定 10件取得
     */
    @Test
    void selectRecentTweets_Success002() {

        // テストデータ
        int limit = 100;

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweets(limit);

        // テスト結果
        assertThat(tweets, is(notNullValue()));
        assertThat(tweets.size(), is(10));
        tweets.forEach(tweet -> assertThat(tweet.getDeleteFlag(), is(0)));

        // datetimeの降順になっているか検証
        for (int i = 0; i < tweets.size() - 1; i++) {
            assertThat(tweets.get(i).getDatetime().isAfter(tweets.get(i + 1).getDatetime()) ||
                    tweets.get(i).getDatetime().isEqual(tweets.get(i + 1).getDatetime()), is(true));
        }
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void selectRecentTweets_Success003() {

        // クリアデータ
        tweetRepository.deleteAll();

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweets(5);

        // テスト結果
        assertThat(tweets.size(), is(0));
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 5件指定 5件取得
     */
    @Test
    void selectRecentTweetsByAccountId_Success001() {

        // テストデータ
        String accountId = "q30387";
        int limit = 5;

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId(accountId, limit);
        assertThat(tweets, is(notNullValue()));
        assertThat(tweets.size(), is(5));
        tweets.forEach(tweet -> {
            assertThat(tweet.getAccountId(), is(accountId));
            assertThat(tweet.getDeleteFlag(), is(0));
        });

        // datetimeの降順になっているか検証
        for (int i = 0; i < tweets.size() - 1; i++) {
            assertThat(tweets.get(i).getDatetime().isAfter(tweets.get(i + 1).getDatetime()) ||
                    tweets.get(i).getDatetime().isEqual(tweets.get(i + 1).getDatetime()), is(true));
        }
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 100件指定 5件取得
     */
    @Test
    void selectRecentTweetsByAccountId_Success002() {

        // テストデータ
        String accountId = "q30387";
        int limit = 100;

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId(accountId, limit);

        // テスト結果
        assertThat(tweets, is(notNullValue()));
        assertThat(tweets.size(), is(5));
        tweets.forEach(tweet -> {
            assertThat(tweet.getAccountId(), is(accountId));
            assertThat(tweet.getDeleteFlag(), is(0));
        });

        // datetimeの降順になっているか検証
        for (int i = 0; i < tweets.size() - 1; i++) {
            assertThat(tweets.get(i).getDatetime().isAfter(tweets.get(i + 1).getDatetime()) ||
                    tweets.get(i).getDatetime().isEqual(tweets.get(i + 1).getDatetime()), is(true));
        }
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void selectRecentTweetsByAccountId_Success003() {

        // クリアデータ
        tweetRepository.deleteAll();

        // テスト実行
        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId("q30387", 5);

        // テスト結果
        assertThat(tweets.size(), is(0));
    }

    /**
     * ツイート登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void insert_Success001() {

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);
        TweetRecord tweetRecord = new TweetRecord(11, "user_C", "新規投稿テスト", "/img/new.jpg", 0, 0, 0, 0, now, "渋谷区", 0);

        // テスト実行
        int result = tweetRepository.insert(tweetRecord);

        // テスト結果
        assertThat(result, is(1));

        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId("user_C", 1);
        TweetRecord tweet = tweets.get(0);
        assertThat(tweet.getAccountId(), is("user_C"));
        assertThat(tweet.getText(), is("新規投稿テスト"));
        assertThat(tweet.getImage(), is("/img/new.jpg"));
        assertThat(tweet.getLikes(), is(0));
        assertThat(tweet.getRetweets(), is(0));
        assertThat(tweet.getReplies(), is(0));
        assertThat(tweet.getViews(), is(0));
        assertThat(tweet.getDatetime(), is(now));
        assertThat(tweet.getLocation(), is("渋谷区"));
        assertThat(tweet.getDeleteFlag(), is(0));
    }

    /**
     * ツイート更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void update_Success001() {

        // テストデータ
        int tweetId = 1;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        TweetRecord tweetRecord = new TweetRecord(tweetId, "user_A", "更新テキスト", "", 99, 88, 77, 66, now, "秋葉原", 0);

        // テスト実行
        int result = tweetRepository.update(tweetId, tweetRecord);

        // テスト結果
        assertThat(result, is(1));

        TweetRecord tweet = tweetRepository.selectTweet(1);
        assertThat(tweet.getText(), is("更新テキスト"));
        assertThat(tweet.getImage(), is(""));
        assertThat(tweet.getLikes(), is(99));
        assertThat(tweet.getRetweets(), is(88));
        assertThat(tweet.getReplies(), is(77));
        assertThat(tweet.getViews(), is(66));
        assertThat(tweet.getLocation(), is("秋葉原"));
        assertThat(tweet.getDeleteFlag(), is(0));
    }

    /**
     * ツイート削除
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void delete_Success001() {

        // テストデータ
        int tweetId = 1;

        // テスト実行
        int result = tweetRepository.delete(tweetId);

        // テスト結果
        assertThat(result, is(1));
        TweetRecord tweet = tweetRepository.selectTweet(tweetId);
        assertThat(tweet, is(nullValue()));
    }
}
