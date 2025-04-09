package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.domain.repository.TweetScheduleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TweetScheduleServiceTest {

    @InjectMocks
    TweetScheduleService tweetScheduleService;

    @Mock
    TweetScheduleRepository tweetScheduleRepository;

    /**
     * 予約ツイート1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getScheduledTweet_Success001() {

        // モックデータ
        ScheduledTweetRecord mock = createRecord(1, "userA", "予定", "img", "Tokyo", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(1)).thenReturn(mock);

        // テスト実行
        ScheduledTweetRecord result = tweetScheduleService.getScheduledTweet(1);

        //　テスト結果
        assertScheduledTweet(result, mock);
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(1);
    }

    /**
     * 予約ツイート1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getScheduledTweet_Error001() {

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(999)).thenReturn(null);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.getScheduledTweet(999));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(999);
    }

    /**
     * 特定アカウントの予約ツイート取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getScheduledTweetsByAccountId_Success001() {

        // モックデータ
        List<ScheduledTweetRecord> mockList = Arrays.asList(
                createRecord(1, "userA", "予定1", null, "Tokyo", 0),
                createRecord(2, "userA", "予定2", null, "Tokyo", 0)
        );

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweetsByAccountId("userA")).thenReturn(mockList);

        // テスト実行
        List<ScheduledTweetRecord> result = tweetScheduleService.getScheduledTweetsByAccountId("userA");

        //　テスト結果
        assertThat(result.size(), is(2));
        assertScheduledTweet(result.get(0), mockList.get(0));
        assertScheduledTweet(result.get(1), mockList.get(1));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweetsByAccountId("userA");
    }

    /**
     * 予約ツイート登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void scheduleTweet_Success001() {

        // モックデータ
        ScheduledTweetRecord input = createRecord(0, "userB", "新規", null, "Osaka", 0);

        // モック設定
        when(tweetScheduleRepository.insert(input)).thenReturn(1);

        // テスト実行
        int result = tweetScheduleService.scheduleTweet(input);

        //　テスト結果
        assertThat(result, is(1));
        verify(tweetScheduleRepository, times(1)).insert(input);
    }

    /**
     * 予約ツイート登録
     * ケース：異常系
     * コンディション：登録失敗
     */
    @Test
    void scheduleTweet_Error001() {

        // モックデータ
        ScheduledTweetRecord input = createRecord(0, "userB", "新規", null, "Osaka", 0);

        // モック設定
        when(tweetScheduleRepository.insert(input)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.scheduleTweet(input));
        verify(tweetScheduleRepository, times(1)).insert(input);
    }

    /**
     * 予約ツイート更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void updateSchedule_Success001() {

        // モックデータ
        ScheduledTweetRecord input = createRecord(1, "userC", "更新内容", null, "Tokyo", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(1)).thenReturn(input);
        when(tweetScheduleRepository.update(1, input)).thenReturn(1);

        // テスト実行
        int result = tweetScheduleService.updateSchedule(1, input);

        //　テスト結果
        assertThat(result, is(1));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(1);
        verify(tweetScheduleRepository, times(1)).update(1, input);
    }

    /**
     * 予約ツイート更新
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void updateSchedule_Error001() {

        // モックデータ
        ScheduledTweetRecord input = createRecord(99, "userX", "無効", null, "Tokyo", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(99)).thenReturn(null);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.updateSchedule(99, input));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(99);
    }

    /**
     * 予約ツイート更新
     * ケース：異常系
     * コンディション：更新失敗
     */
    @Test
    void updateSchedule_Error002() {

        // モックデータ
        ScheduledTweetRecord input = createRecord(2, "userX", "失敗", null, "Tokyo", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(2)).thenReturn(input);
        when(tweetScheduleRepository.update(2, input)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.updateSchedule(2, input));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(2);
        verify(tweetScheduleRepository, times(1)).update(2, input);
    }

    /**
     * 予約ツイートキャンセル
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void cancelSchedule_Success001() {

        // モックデータ
        ScheduledTweetRecord target = createRecord(3, "userZ", "削除", null, "Nara", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(3)).thenReturn(target);
        when(tweetScheduleRepository.delete(3)).thenReturn(1);

        // テスト実行
        int result = tweetScheduleService.cancelSchedule(3);

        //　テスト結果
        assertThat(result, is(1));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(3);
        verify(tweetScheduleRepository, times(1)).delete(3);
    }

    /**
     * 予約ツイートキャンセル
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void cancelSchedule_Error001() {

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(100)).thenReturn(null);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.cancelSchedule(100));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(100);
    }

    /**
     * 予約ツイートキャンセル
     * ケース：異常系
     * コンディション：削除失敗
     */
    @Test
    void cancelSchedule_Error002() {

        // モックデータ
        ScheduledTweetRecord target = createRecord(4, "userY", "削除失敗", null, "Kobe", 0);

        // モック設定
        when(tweetScheduleRepository.selectScheduledTweet(4)).thenReturn(target);
        when(tweetScheduleRepository.delete(4)).thenReturn(0);

        // テスト実行・テスト結果
        assertThrows(TweetException.class, () -> tweetScheduleService.cancelSchedule(4));
        verify(tweetScheduleRepository, times(1)).selectScheduledTweet(4);
        verify(tweetScheduleRepository, times(1)).delete(4);
    }

    private ScheduledTweetRecord createRecord(int id, String accountId, String text, String image, String location, int deleteFlag) {
        return new ScheduledTweetRecord(
                id,
                accountId,
                text,
                image,
                location,
                LocalDateTime.of(2025, 4, 1, 12, 0),
                LocalDateTime.of(2025, 4, 1, 10, 0),
                deleteFlag
        );
    }

    private void assertScheduledTweet(ScheduledTweetRecord actual, ScheduledTweetRecord expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getAccountId(), is(expected.getAccountId()));
        assertThat(actual.getText(), is(expected.getText()));
        assertThat(actual.getImage(), is(expected.getImage()));
        assertThat(actual.getLocation(), is(expected.getLocation()));
        assertThat(actual.getScheduledDatetime(), is(expected.getScheduledDatetime()));
        assertThat(actual.getCreatedDatetime(), is(expected.getCreatedDatetime()));
        assertThat(actual.getDeleteFlag(), is(expected.getDeleteFlag()));
    }
}
