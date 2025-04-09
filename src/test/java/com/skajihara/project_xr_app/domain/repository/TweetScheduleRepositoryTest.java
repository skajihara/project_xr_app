package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
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
class TweetScheduleRepositoryTest {

    @Autowired
    TweetScheduleRepository tweetScheduleRepository;

    /**
     * 予約ツイート1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void selectScheduledTweet_Success001() {
        // テストデータ
        int scheduleId = 1;

        // テスト実行
        ScheduledTweetRecord result = tweetScheduleRepository.selectScheduledTweet(scheduleId);

        // テスト結果
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(scheduleId));
        assertThat(result.getAccountId(), is("user_A"));
        assertThat(result.getText(), is("ツイート内容1"));
        assertThat(result.getImage(), is("/src/assets/images/img01.GIF"));
        assertThat(result.getLocation(), is("東京都"));
        assertThat(result.getDeleteFlag(), is(0));
    }

    /**
     * 予約ツイート1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void selectScheduledTweet_Error001() {
        // テストデータ
        int scheduleId = 999;

        // テスト実行
        ScheduledTweetRecord result = tweetScheduleRepository.selectScheduledTweet(scheduleId);

        // テスト結果
        assertThat(result, is(nullValue()));
    }

    /**
     * 特定アカウントの予約ツイート取得
     * ケース：正常系
     * コンディション：データあり、昇順取得
     */
    @Test
    void selectScheduledTweetsByAccountId_Success001() {
        // テストデータ
        String accountId = "q30387";

        // テスト実行
        List<ScheduledTweetRecord> results = tweetScheduleRepository.selectScheduledTweetsByAccountId(accountId);

        // テスト結果
        assertThat(results, is(notNullValue()));
        assertThat(results.size(), is(3));
        results.forEach(tweet -> {
            assertThat(tweet.getAccountId(), is(accountId));
            assertThat(tweet.getDeleteFlag(), is(0));
        });

        for (int i = 0; i < results.size() - 1; i++) {
            assertThat(results.get(i).getScheduledDatetime().isBefore(results.get(i + 1).getScheduledDatetime()) ||
                    results.get(i).getScheduledDatetime().isEqual(results.get(i + 1).getScheduledDatetime()), is(true));
        }
    }

    /**
     * 特定アカウントの予約ツイート取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void selectScheduledTweetsByAccountId_Success002() {
        // テストデータ
        String accountId = "no_user";

        // テスト実行
        List<ScheduledTweetRecord> results = tweetScheduleRepository.selectScheduledTweetsByAccountId(accountId);

        // テスト結果
        assertThat(results.size(), is(0));
    }

    /**
     * バッチ処理対象ツイート取得
     * ケース：正常系
     * コンディション：対象データあり scheduledDatetime・idの昇順
     */
    @Test
    void selectScheduledTweetsForBatch_Success001() {
        // モックデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);

        ScheduledTweetRecord tweet1 = new ScheduledTweetRecord();
        tweet1.setAccountId("batch_user");
        tweet1.setText("バッチ投稿1");
        tweet1.setImage("/img/batch1.jpg");
        tweet1.setLocation("東京");
        tweet1.setScheduledDatetime(now.minusSeconds(20));
        tweet1.setCreatedDatetime(now.minusMinutes(10));
        tweet1.setDeleteFlag(0);

        ScheduledTweetRecord tweet2 = new ScheduledTweetRecord();
        tweet2.setAccountId("batch_user");
        tweet2.setText("バッチ投稿2");
        tweet2.setImage("/img/batch2.jpg");
        tweet2.setLocation("大阪");
        tweet2.setScheduledDatetime(now.minusSeconds(10)); // 後の時間
        tweet2.setCreatedDatetime(now.minusMinutes(9));
        tweet2.setDeleteFlag(0);

        tweetScheduleRepository.insert(tweet1);
        tweetScheduleRepository.insert(tweet2);

        // 登録された全データ取得して最小IDを特定
        List<ScheduledTweetRecord> all = tweetScheduleRepository.selectScheduledTweetsByAccountId("batch_user");
        int minId = all.stream()
                .mapToInt(ScheduledTweetRecord::getId)
                .min()
                .orElseThrow();

        // テスト実行
        List<ScheduledTweetRecord> result = tweetScheduleRepository.selectScheduledTweetsForBatch(minId - 1, now);

        // テスト結果
        assertThat(result.size(), is(2));

        // 順序検証：scheduledDatetime ASC, id ASC
        for (int i = 0; i < result.size() - 1; i++) {
            ScheduledTweetRecord current = result.get(i);
            ScheduledTweetRecord next = result.get(i + 1);
            LocalDateTime dt1 = current.getScheduledDatetime();
            LocalDateTime dt2 = next.getScheduledDatetime();
            int id1 = current.getId();
            int id2 = next.getId();

            if (dt1.isEqual(dt2)) {
                assertThat(id1 < id2, is(true));
            } else {
                assertThat(dt1.isBefore(dt2), is(true));
            }
        }
    }

    /**
     * バッチ処理対象ツイート取得
     * ケース：正常系
     * コンディション：対象データなし
     */
    @Test
    void selectScheduledTweetsForBatch_Success002() {

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);
        int lastProcessedId = Integer.MAX_VALUE;
        // テスト実行
        List<ScheduledTweetRecord> result = tweetScheduleRepository.selectScheduledTweetsForBatch(lastProcessedId, now);

        // テスト結果
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(0));
    }

    /**
     * 予約ツイート登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void insert_Success001() {

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ScheduledTweetRecord record = new ScheduledTweetRecord();
        record.setAccountId("test_user");
        record.setText("予約投稿テスト");
        record.setImage("/img/test.jpg");
        record.setLocation("名古屋");
        record.setScheduledDatetime(now.plusDays(1));
        record.setCreatedDatetime(now);
        record.setDeleteFlag(0);

        // テスト実行
        int result = tweetScheduleRepository.insert(record);

        // テスト結果
        assertThat(result, is(1));
        List<ScheduledTweetRecord> list = tweetScheduleRepository.selectScheduledTweetsByAccountId("test_user");
        assertThat(list.size(), is(1));
        ScheduledTweetRecord saved = list.get(0);
        assertThat(saved.getAccountId(), is("test_user"));
        assertThat(saved.getText(), is("予約投稿テスト"));
        assertThat(saved.getImage(), is("/img/test.jpg"));
        assertThat(saved.getLocation(), is("名古屋"));
        assertThat(saved.getDeleteFlag(), is(0));
    }

    /**
     * 予約ツイート更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void update_Success001() {

        // 対象データの存在確認
        ScheduledTweetRecord original = tweetScheduleRepository.selectScheduledTweet(1);
        assertThat(original, is(notNullValue()));

        // テストデータ
        ScheduledTweetRecord update = new ScheduledTweetRecord();
        update.setText("更新内容");
        update.setImage("/img/updated.jpg");
        update.setLocation("札幌");
        update.setScheduledDatetime(original.getScheduledDatetime().plusDays(1));
        update.setCreatedDatetime(original.getCreatedDatetime());
        update.setDeleteFlag(0);

        // テスト実行
        int result = tweetScheduleRepository.update(1, update);

        // テスト結果
        assertThat(result, is(1));
        ScheduledTweetRecord updated = tweetScheduleRepository.selectScheduledTweet(1);
        assertThat(updated.getText(), is("更新内容"));
        assertThat(updated.getImage(), is("/img/updated.jpg"));
        assertThat(updated.getLocation(), is("札幌"));
        assertThat(updated.getScheduledDatetime(), is(original.getScheduledDatetime().plusDays(1)));
        assertThat(updated.getCreatedDatetime(), is(original.getCreatedDatetime()));
        assertThat(updated.getDeleteFlag(), is(0));
    }

    /**
     * 予約ツイート削除
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void delete_Success001() {

        // 対象データの存在確認
        ScheduledTweetRecord target = tweetScheduleRepository.selectScheduledTweet(2);
        assertThat(target, is(notNullValue()));
        assertThat(target.getDeleteFlag(), is(0));

        // テスト実行
        int result = tweetScheduleRepository.delete(2);

        // テスト結果
        assertThat(result, is(1));
        ScheduledTweetRecord deleted = tweetScheduleRepository.selectScheduledTweet(2);
        assertThat(deleted, is(nullValue()));
    }
}
