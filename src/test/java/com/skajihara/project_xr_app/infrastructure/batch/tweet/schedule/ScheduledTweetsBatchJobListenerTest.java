package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import com.skajihara.project_xr_app.domain.repository.BatchHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ScheduledTweetsBatchJobListenerTest {

    @Mock
    private BatchHistoryRepository batchHistoryRepository;

    @InjectMocks
    private ScheduledTweetsBatchJobListener listener;

    /**
     * 予約ツイートバッチ前処理
     * ケース：正常系
     * コンディション：バッチ履歴データあり
     */
    @Test
    void beforeJob_Success001() {

        // モックデータ
        JobExecution jobExecution = new JobExecution(1L);
        BatchHistoryRecord latest = new BatchHistoryRecord(
                100, "scheduledTweetsPostingJob", 300, 5,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().minusSeconds(10), 1
        );

        // モック設定
        when(batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob"))
                .thenReturn(latest);
        when(batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob"))
                .thenReturn(new BatchHistoryRecord(101, "scheduledTweetsPostingJob", 300, 0,
                        LocalDateTime.now(), null, 0));

        // テスト実行
        listener.beforeJob(jobExecution);

        // テスト結果
        assertThat(jobExecution.getExecutionContext().containsKey("batchHistoryId"), is(true));
        int batchHistoryId = jobExecution.getExecutionContext().getInt("batchHistoryId");
        assertThat(batchHistoryId, is(101));
        verify(batchHistoryRepository, times(2)).selectLatestRecord("scheduledTweetsPostingJob");
        verify(batchHistoryRepository, times(1)).insert(any());
    }

    /**
     * 予約ツイートバッチ前処理
     * ケース：正常系
     * コンディション：バッチ履歴データなし
     */
    @Test
    void beforeJob_Success002() {

        // モックデータ
        JobExecution jobExecution = new JobExecution(1L);
        BatchHistoryRecord inserted = new BatchHistoryRecord(
                101, "scheduledTweetsPostingJob", 0, 0,
                LocalDateTime.now(), null, 0
        );

        // モック設定
        when(batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob")).thenReturn(null);
        when(batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob")).thenReturn(inserted);

        // テスト実行
        listener.beforeJob(jobExecution);

        // テスト結果
        int id = jobExecution.getExecutionContext().getInt("batchHistoryId");
        assertThat(id, is(101));
        verify(batchHistoryRepository, times(2)).selectLatestRecord("scheduledTweetsPostingJob");
        verify(batchHistoryRepository, times(1)).insert(any());
    }

    /**
     * 予約ツイートバッチ後処理
     * ケース：正常系
     * コンディション：正常終了
     */
    @Test
    void afterJob_Success001() {

        // モックデータ
        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobExecution.getExecutionContext().putInt("batchHistoryId", 101);

        BatchHistoryRecord history = new BatchHistoryRecord(
                101, "scheduledTweetsPostingJob", 300, 5,
                LocalDateTime.now().minusMinutes(1), null, 0
        );

        // モック設定
        when(batchHistoryRepository.selectByPrimaryKey(101)).thenReturn(history);

        // テスト実行
        listener.afterJob(jobExecution);

        // テスト結果
        assertThat(history.getSucceeded(), is(1));
        assertThat(history.getExecutionEnd(), is(notNullValue()));
        verify(batchHistoryRepository, times(1)).selectByPrimaryKey(101);
        verify(batchHistoryRepository, times(1)).update(eq(101), any(BatchHistoryRecord.class));
    }

    /*
     * 予約ツイートバッチ後処理
     * ケース：異常系
     * コンディション：異常終了
     */
    @Test
    void afterJob_Error001() {

        // モックデータ
        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.setStatus(BatchStatus.FAILED);
        jobExecution.getExecutionContext().putInt("batchHistoryId", 202);

        BatchHistoryRecord history = new BatchHistoryRecord(
                202, "scheduledTweetsPostingJob", 999, 9,
                LocalDateTime.now().minusMinutes(1), null, 0
        );

        // モック設定
        when(batchHistoryRepository.selectByPrimaryKey(202)).thenReturn(history);

        // テスト実行
        listener.afterJob(jobExecution);

        // テスト結果
        assertThat(history.getSucceeded(), is(0));
        assertThat(history.getExecutionEnd(), is(notNullValue()));
        verify(batchHistoryRepository, times(1)).selectByPrimaryKey(202);
        verify(batchHistoryRepository, times(1)).update(eq(202), any(BatchHistoryRecord.class));
    }

    /**
     * 予約ツイートバッチ後処理
     * ケース：異常系
     * コンディション：バッチ履歴IDなし
     */
    @Test
    void afterJob_Error002() {

        // モックデータ
        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.setStatus(BatchStatus.FAILED);

        // テスト実行
        listener.afterJob(jobExecution);

        // テスト結果
        verify(batchHistoryRepository, never()).selectByPrimaryKey(anyInt());
        verify(batchHistoryRepository, never()).update(anyInt(), any(BatchHistoryRecord.class));
    }
}
