package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.BatchHistoryRepository;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import com.skajihara.project_xr_app.domain.repository.TweetScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@SpringBatchTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduledTweetsBatchConfigTest {

    // レコードにもテーブルにも文字数やNULL禁止を設定しているため
    // Tasklet内で自然なエラーを起こせないはず
    // 条件設定が複雑な割に意味のあるテストとは言えない気がするため
    // TaskletやRepositoryをモックするような不自然なエラーはテスト対象外とする

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    BatchHistoryRepository batchHistoryRepository;

    @Autowired
    TweetScheduleRepository tweetScheduleRepository;

    @Autowired
    @Qualifier("scheduledTweetsPostingJob")
    private Job scheduledTweetsPostingJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(scheduledTweetsPostingJob);
    }

    @Test
    void scheduledTweetsPostingJob_success() throws Exception {

        // クリアデータ
        tweetRepository.deleteAll();
        tweetScheduleRepository.deleteAll();
        batchHistoryRepository.deleteAll();

        // テストデータセットアップ
        // 予約ツイート登録
        ScheduledTweetRecord scheduled = new ScheduledTweetRecord(
                null,
                "q30387",
                "統合テスト投稿です！",
                "/img/test.jpg",
                "テスト市",
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now(),
                0
        );
        tweetScheduleRepository.insert(scheduled);

        // 登録した予約ツイートID取得
        int scheduleId = tweetScheduleRepository.selectScheduledTweetsByAccountId("q30387").stream()
                .filter(t -> t.getText().equals("統合テスト投稿です！"))
                .findFirst().orElseThrow().getId();

        // バッチ履歴（前回成功履歴）登録
        BatchHistoryRecord prevHistory = new BatchHistoryRecord(
                null, "scheduledTweetsPostingJob", 0, 0,
                LocalDateTime.now().minusMinutes(5), LocalDateTime.now().minusMinutes(4), 1
        );
        batchHistoryRepository.insert(prevHistory);

        // テスト実行
        jobLauncherTestUtils.setJob(scheduledTweetsPostingJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // ジョブは正常終了したか？
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        // ステップが1つだけ呼ばれたか？
        List<StepExecution> steps = jobExecution.getStepExecutions().stream().toList();
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0).getStepName(), is("scheduledTweetsPostingStep"));

        // ツイートがテーブルに登録されたか？
        List<TweetRecord> tweets = tweetRepository.selectRecentTweetsByAccountId("q30387", 5);
        boolean exists = tweets.stream().anyMatch(t -> t.getText().equals("統合テスト投稿です！"));
        assertThat(exists, is(true));

        // 対象の予約ツイートが削除済みか？
        ScheduledTweetRecord updatedSchedule = tweetScheduleRepository.findAll().stream()
                .filter(t -> t.getId() == scheduleId)
                .findFirst().orElseThrow();
        assertThat(updatedSchedule.getDeleteFlag(), is(1));

        // バッチ履歴が更新されたか？
        BatchHistoryRecord latestHistory = batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob");
        assertThat(latestHistory.getSucceeded(), is(1));
        assertThat(latestHistory.getProcessedNum(), is(1));
        assertThat(latestHistory.getLatestProcessedId(), is(scheduleId));
        assertThat(latestHistory.getExecutionEnd(), is(notNullValue()));
    }

    @Test
    void scheduledTweetsPostingJob_success_whenNoTargetData() throws Exception {

        // クリアデータ
        tweetRepository.deleteAll();
        tweetScheduleRepository.deleteAll();
        batchHistoryRepository.deleteAll();

        // バッチ履歴登録（対象0件になるように latest_processed_id を大きく設定）
        BatchHistoryRecord history = new BatchHistoryRecord(
                null,
                "scheduledTweetsPostingJob",
                999999,
                0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(9),
                1
        );
        batchHistoryRepository.insert(history);

        // テスト実行
        jobLauncherTestUtils.setJob(scheduledTweetsPostingJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // ジョブは正常終了してるか？
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        // ステップが1つだけ呼ばれたか？
        List<StepExecution> steps = jobExecution.getStepExecutions().stream().toList();
        assertThat(steps.size(), is(1));
        assertThat(steps.get(0).getStepName(), is("scheduledTweetsPostingStep"));

        // バッチ履歴が正常として更新されたか？
        BatchHistoryRecord latest = batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob");
        assertThat(latest.getSucceeded(), is(1));
        assertThat(latest.getProcessedNum(), is(0));
        assertThat(latest.getExecutionEnd(), is(notNullValue()));
    }

}
