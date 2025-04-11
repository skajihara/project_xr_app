package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule;

import com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule.step.ScheduledTweetsPostingTasklet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@AllArgsConstructor
public class ScheduledTweetsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ScheduledTweetsBatchJobListener jobListener;

    /**
     * 予約ツイート送信ジョブ
     */
    @Bean
    public Job scheduledTweetsPostingJob(@Qualifier("scheduledTweetsPostingStep") Step scheduledTweetPostingStep) {
        return new JobBuilder("scheduledTweetsPostingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .listener(jobListener)
                .start(scheduledTweetPostingStep)
                .build();
    }

    /**
     * 予約ツイート送信ステップ
     *
     * @param scheduledTweetsPostingTasklet 内部実装
     * @return 予約ツイート送信ステップ
     */
    @Bean
    public Step scheduledTweetsPostingStep(ScheduledTweetsPostingTasklet scheduledTweetsPostingTasklet) {
        return new StepBuilder("scheduledTweetsPostingStep", jobRepository)
                .tasklet(scheduledTweetsPostingTasklet, platformTransactionManager)
                .build();
    }
}
