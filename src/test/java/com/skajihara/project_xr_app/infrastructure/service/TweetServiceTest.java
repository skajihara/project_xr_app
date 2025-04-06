package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import com.skajihara.project_xr_app.exception.TweetException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TweetServiceTest {

    @InjectMocks
    TweetService tweetService;

    @Mock
    TweetRepository tweetRepository;

    /**
     * ツイート1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getTweet_Success001() {

        // モックデータ
        TweetRecord mockTweet = new TweetRecord(1, "acc1", "text", "img", 1, 2, 3, 4,
                LocalDateTime.of(2023, 1, 1, 10, 0), "Tokyo", 0);

        // モック設定
        when(tweetRepository.selectTweet(1)).thenReturn(mockTweet);

        // テスト実行
        TweetRecord result = tweetService.getTweet(1);

        // テスト結果
        assertTweet(result, mockTweet);
        verify(tweetRepository).selectTweet(1);
    }

    /**
     * ツイート1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getTweet_Error001() {

        // モック設定
        when(tweetRepository.selectTweet(999)).thenReturn(null);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetService.getTweet(999));
        verify(tweetRepository).selectTweet(999);
    }

    /**
     * ツイート全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getAllTweets_Success001() {

        // モックデータ
        TweetRecord t1 = new TweetRecord(1, "acc1", "text1", "img1", 1, 2, 3, 4, LocalDateTime.now(), "Tokyo", 0);
        TweetRecord t2 = new TweetRecord(2, "acc2", "text2", "img2", 5, 6, 7, 8, LocalDateTime.now(), "Osaka", 0);
        List<TweetRecord> mockList = Arrays.asList(t1, t2);

        // モック設定
        when(tweetRepository.selectAllTweets()).thenReturn(mockList);

        // テスト実行
        List<TweetRecord> result = tweetService.getAllTweets();

        // テスト結果
        assertThat(result.size(), is(2));
        assertTweet(result.get(0), t1);
        assertTweet(result.get(1), t2);
        verify(tweetRepository).selectAllTweets();
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getRecentTweets_Success001() {

        // モックデータ
        TweetRecord t1 = new TweetRecord(3, "acc3", "text3", "img3", 9, 10, 11, 12, LocalDateTime.now(), "Kyoto", 0);
        TweetRecord t2 = new TweetRecord(4, "acc4", "text4", "img4", 13, 14, 15, 16, LocalDateTime.now(), "Nara", 0);
        List<TweetRecord> mockList = Arrays.asList(t1, t2);

        // モック設定
        when(tweetRepository.selectRecentTweets(5)).thenReturn(mockList);

        // テスト実行
        List<TweetRecord> result = tweetService.getRecentTweets(5);

        // テスト結果
        assertThat(result.size(), is(2));
        assertTweet(result.get(0), t1);
        assertTweet(result.get(1), t2);
        verify(tweetRepository).selectRecentTweets(5);
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getRecentTweetsByAccountId_Success001() {

        // モックデータ
        TweetRecord t1 = new TweetRecord(5, "acc5", "text5", "img5", 17, 18, 19, 20, LocalDateTime.now(), "Sapporo", 0);
        TweetRecord t2 = new TweetRecord(6, "acc5", "text6", "img6", 21, 22, 23, 24, LocalDateTime.now(), "Hakodate", 0);
        List<TweetRecord> mockList = Arrays.asList(t1, t2);

        // モック設定
        when(tweetRepository.selectRecentTweetsByAccountId("acc5", 5)).thenReturn(mockList);

        // テスト実行
        List<TweetRecord> result = tweetService.getTweetsByAccountId("acc5", 5);

        // テスト結果
        assertThat(result.size(), is(2));
        assertTweet(result.get(0), t1);
        assertTweet(result.get(1), t2);
        verify(tweetRepository).selectRecentTweetsByAccountId("acc5", 5);
    }

    /**
     * ツイート登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void insert_Success001() {

        // モックデータ
        TweetRecord tweet = new TweetRecord(0, "accX", "newText", "newImg", 0, 0, 0, 0,
                LocalDateTime.now(), "Nagoya", 0);

        // モック設定
        when(tweetRepository.insert(tweet)).thenReturn(1);

        // テスト実行
        int result = tweetService.postTweet(tweet);

        // テスト結果
        assertThat(result, is(1));
        verify(tweetRepository).insert(tweet);
    }

    /**
     * ツイート登録
     * ケース：異常系
     * コンディション：登録失敗
     */
    @Test
    void insert_Error001() {

        // モックデータ
        TweetRecord tweet = new TweetRecord(0, "accX", "fail", "img", 0, 0, 0, 0,
                LocalDateTime.now(), "", 0);

        // モック設定
        when(tweetRepository.insert(tweet)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetService.postTweet(tweet));
        verify(tweetRepository).insert(tweet);
    }


    /**
     * ツイート更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void update_Success001() {

        // モックデータ
        TweetRecord input = new TweetRecord(10, "accY", "upText", "upImg", 1, 1, 1, 1,
                LocalDateTime.now(), "Sendai", 0);

        // モック設定
        when(tweetRepository.selectTweet(10)).thenReturn(input);
        when(tweetRepository.update(10, input)).thenReturn(1);

        // テスト実行
        int result = tweetService.updateTweet(10, input);

        // テスト結果
        assertThat(result, is(1));
        verify(tweetRepository).selectTweet(10);
        verify(tweetRepository).update(10, input);
    }

    /**
     * ツイート更新
     * ケース：異常系
     * コンディション：更新失敗
     */
    @Test
    void update_Error001() {

        // モックデータ
        TweetRecord input = new TweetRecord(10, "accY", "upText", "upImg", 1, 1, 1, 1,
                LocalDateTime.now(), "Sendai", 0);

        // モック設定
        when(tweetRepository.selectTweet(10)).thenReturn(input);
        when(tweetRepository.update(10, input)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetService.updateTweet(10, input));
        verify(tweetRepository).update(10, input);
    }


    /**
     * ツイート削除
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void delete_Success001() {

        // モックデータ
        TweetRecord target = new TweetRecord(20, "accZ", "del", "img", 0, 0, 0, 0,
                LocalDateTime.now(), "Okinawa", 0);

        // モック設定
        when(tweetRepository.selectTweet(20)).thenReturn(target);
        when(tweetRepository.delete(20)).thenReturn(1);

        // テスト実行
        int result = tweetService.deleteTweet(20);

        // テスト結果
        assertThat(result, is(1));
        verify(tweetRepository).delete(20);
    }

    /**
     * ツイート削除
     * ケース：異常系
     * コンディション：削除失敗
     */
    @Test
    void delete_Error001() {

        // モックデータ
        TweetRecord target = new TweetRecord(20, "accZ", "del", "img", 0, 0, 0, 0,
                LocalDateTime.now(), "Okinawa", 0);

        // モック設定
        when(tweetRepository.selectTweet(20)).thenReturn(target);
        when(tweetRepository.delete(20)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetService.deleteTweet(20));
        verify(tweetRepository).delete(20);
    }

    private void assertTweet(TweetRecord actual, TweetRecord expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getAccountId(), is(expected.getAccountId()));
        assertThat(actual.getText(), is(expected.getText()));
        assertThat(actual.getImage(), is(expected.getImage()));
        assertThat(actual.getLikes(), is(expected.getLikes()));
        assertThat(actual.getRetweets(), is(expected.getRetweets()));
        assertThat(actual.getReplies(), is(expected.getReplies()));
        assertThat(actual.getViews(), is(expected.getViews()));
        assertThat(actual.getDatetime(), is(expected.getDatetime()));
        assertThat(actual.getLocation(), is(expected.getLocation()));
        assertThat(actual.getDeleteFlag(), is(expected.getDeleteFlag()));
    }
}
