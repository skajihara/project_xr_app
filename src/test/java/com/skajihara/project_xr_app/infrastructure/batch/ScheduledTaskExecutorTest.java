package com.skajihara.project_xr_app.infrastructure.batch;

// @SpringBootTestを使うのは厳しいためJunitのみでテスト

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ScheduledTaskExecutor.class, ScheduledTaskExecutorTest.MockConfig.class})
class ScheduledTaskExecutorTest {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("scheduledTweetsPostingJob")
    Job scheduledTweetsPostingJob;

    @Autowired
    ScheduledTaskExecutor executor;

    private JobExecution mockExecution;

    @BeforeEach
    void setUp() throws Exception {
        // モックの初期化（呼び出し回数リセット）
        mockExecution = mock(JobExecution.class);
        reset(jobLauncher);
        when(jobLauncher.run(any(), any())).thenReturn(mockExecution);
    }

    /**
     * ScheduledTweetsPostingJob
     * ケース：正常系
     * コンディション：ジョブ成功
     */
    @Test
    void scheduledTweetsPostingJob_Success001() throws Exception {

        // モック設定
        when(mockExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // テスト実行
        executor.executeScheduledTweetsPostingJob();

        // テスト結果
        verify(jobLauncher, times(1)).run(eq(scheduledTweetsPostingJob), any(JobParameters.class));
        verify(mockExecution, atLeastOnce()).getStatus();
    }

    /**
     * ScheduledTweetsPostingJob
     * ケース：異常系
     * コンディション：ジョブ失敗
     */
    @Test
    void scheduledTweetsPostingJob_Error001() throws Exception {

        // モック設定
        when(mockExecution.getStatus()).thenReturn(BatchStatus.FAILED);

        // テスト実行
        executor.executeScheduledTweetsPostingJob();

        // テスト結果
        verify(jobLauncher, times(1)).run(eq(scheduledTweetsPostingJob), any(JobParameters.class));
        verify(mockExecution, atLeastOnce()).getStatus();
    }

    /**
     * ScheduledTweetsPostingJob
     * ケース：正常系
     * コンディション：例外スロー
     */
    @Test
    void scheduledTweetsPostingJob_Error002() throws Exception {

        // モック設定
        when(jobLauncher.run(any(), any())).thenThrow(new RuntimeException("boom"));

        // テスト実行
        executor.executeScheduledTweetsPostingJob();

        // テスト結果
        verify(jobLauncher, times(1)).run(eq(scheduledTweetsPostingJob), any(JobParameters.class));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public JobLauncher jobLauncher() {
            return mock(JobLauncher.class);
        }

        @Bean(name = "scheduledTweetsPostingJob")
        public Job scheduledTweetsPostingJob() {
            return mock(Job.class);
        }
    }
}
