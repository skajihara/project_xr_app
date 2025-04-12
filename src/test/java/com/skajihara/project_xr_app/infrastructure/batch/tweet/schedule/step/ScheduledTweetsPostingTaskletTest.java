package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule.step;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.BatchHistoryRepository;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import com.skajihara.project_xr_app.domain.repository.TweetScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@SpringBatchTest
@Transactional
class ScheduledTweetsPostingTaskletTest {

    @Autowired
    ScheduledTweetsPostingTasklet tasklet;

    @Autowired
    BatchHistoryRepository batchHistoryRepository;

    @Autowired
    TweetScheduleRepository tweetScheduleRepository;

    @Autowired
    TweetRepository tweetRepository;

    /**
     * 予約ツイート投稿処理
     * ケース：正常系
     * コンディション：投稿対象データあり
     */
    @Test
    void execute_Success001() {

        // クリアデータ
        batchHistoryRepository.deleteAll();
        tweetScheduleRepository.deleteAll();

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);

        // step1：バッチ履歴を登録
        BatchHistoryRecord record = new BatchHistoryRecord(
                null, "scheduledTweetsPostingJob", 0, 0,
                now, now.plusMinutes(1), 0
        );
        batchHistoryRepository.insert(record);

        // step2：insertしたバッチ履歴を取得しなおす
        BatchHistoryRecord inserted = batchHistoryRepository.selectByPrimaryKey(
                // 確実にstep1で登録したバッチ履歴IDを取得するため
                batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob").getId()
        );
        int historyId = inserted.getId();

        // step3：ExecutionContextにバッチ履歴IDをセット
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().putInt("batchHistoryId", historyId);

        // step4：予約ツイートを登録（対象となるように予約日時を過去に設定）
        ScheduledTweetRecord scheduled = new ScheduledTweetRecord(
                null,
                "q30387",
                "これはテスト投稿です！",
                "/test/image.jpg",
                "テスト市",
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now(),
                0
        );
        tweetScheduleRepository.insert(scheduled);

        // step5：登録した予約ツイートのIDを取得
        int scheduleId = tweetScheduleRepository.selectScheduledTweetsByAccountId("q30387").stream()
                .filter(t -> t.getText().equals("これはテスト投稿です！"))
                .findFirst().orElseThrow().getId();

        // step6：テスト実行（処理対象は1件）
        tasklet.execute(null, new ChunkContext(new StepContext(stepExecution)));

        // step7：テスト結果（ツイートの登録、対応する予約ツイートの削除、バッチ履歴の更新）
        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId("q30387", 5);
        boolean exists = tweets.stream().anyMatch(t -> t.getText().equals("これはテスト投稿です！"));
        assertThat(exists, is(true));

        // 削除された予約ツイートは作成したカスタムメソッドでは取得対象外となるため標準メソッドで取得
        List<ScheduledTweetRecord> scheduledTweets = tweetScheduleRepository.findAll();
        ScheduledTweetRecord updatedSchedule = scheduledTweets.stream()
                .filter(t -> t.getId() == scheduleId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("updatedSchedule not found"));
        assertThat(updatedSchedule.getDeleteFlag(), is(1));

        BatchHistoryRecord updatedHistory = batchHistoryRepository.selectByPrimaryKey(historyId);
        assertThat(updatedHistory.getLatestProcessedId(), is(scheduleId));
        assertThat(updatedHistory.getProcessedNum(), is(1));
    }

    /**
     * 予約ツイート投稿処理
     * ケース：正常系
     * コンディション：投稿対象データなし
     */
    @Test
    void execute_Success002() {

        // クリアデータ
        batchHistoryRepository.deleteAll();
        tweetScheduleRepository.deleteAll();

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);

        // step1：バッチ履歴を登録（対象が0件になるように最新IDを高く設定）
        BatchHistoryRecord record = new BatchHistoryRecord(
                null,
                "scheduledTweetsPostingJob",
                999999,
                0,
                now.minusMinutes(10),
                now.minusMinutes(1),
                0
        );
        batchHistoryRepository.insert(record);

        // step2：insertしたバッチ履歴を取得しなおす
        BatchHistoryRecord inserted = batchHistoryRepository.selectByPrimaryKey(
                // 確実にstep1で登録したバッチ履歴IDを取得するため
                batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob").getId()
        );
        int historyId = inserted.getId();

        // step3：ExecutionContextにバッチ履歴IDをセット
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().putInt("batchHistoryId", historyId);

        // step4：テスト実行（処理対象は0件）
        tasklet.execute(null, new ChunkContext(new StepContext(stepExecution)));

        // step5：テスト結果（バッチ履歴の更新）
        BatchHistoryRecord updated = batchHistoryRepository.selectByPrimaryKey(historyId);
        assertThat(updated.getLatestProcessedId(), is(record.getLatestProcessedId()));
        assertThat(updated.getProcessedNum(), is(record.getProcessedNum()));
    }

    /**
     * 予約ツイート投稿処理
     * ケース：異常系
     * コンディション：ExecutionContextにbatchHistoryIdが存在しない
     */
    @Test
    void execute_Error001() {

        // ExecutionContextにbatchHistoryIdを登録しない
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        // テスト実行・テスト結果
        Exception exception = assertThrows(IllegalStateException.class, () ->
                tasklet.execute(null, new ChunkContext(new StepContext(stepExecution)))
        );
        assertThat(exception.getMessage(), containsString("history id does not exist"));
    }

    /**
     * 予約ツイート投稿処理
     * ケース：異常系
     * コンディション：存在しないbatchHistoryIdを指定
     */
    @Test
    void execute_Error002() {

        // ExecutionContextに存在しないbatchHistoryId（999999）を登録
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().putInt("batchHistoryId", 999999);

        // テスト実行・テスト結果
        Exception exception = assertThrows(IllegalStateException.class, () ->
                tasklet.execute(null, new ChunkContext(new StepContext(stepExecution)))
        );
        assertThat(exception.getMessage(), containsString("batch history does not exist"));
    }
}
