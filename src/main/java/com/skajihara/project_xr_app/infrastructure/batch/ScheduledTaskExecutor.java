package com.skajihara.project_xr_app.infrastructure.batch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledTaskExecutor {

    private final JobLauncher jobLauncher;

    @Qualifier("scheduledTweetsPostingJob")
    private final Job scheduledTweetsPostingJob;

    @Scheduled(fixedDelay = 10000)
    public void executeScheduledTweetsPostingJob() {

        LocalDateTime timeStamp = LocalDateTime.now();
        LocalDateTime startTime = timeStamp.withNano(0);
        log.info("【START】scheduled tweets posting job - {}", startTime);

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", timeStamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(scheduledTweetsPostingJob, jobParameters);
            log.info("【" + execution.getStatus() + "】scheduled tweets posting job.");
        } catch (Exception e) {
            log.error("【ERROR】scheduled tweets posting job.");
        } finally {
            LocalDateTime endTime = LocalDateTime.now().withNano(0);
            long durationSec = Duration.between(startTime, endTime).getSeconds();
            log.info("duration : {}s, ( start : {}, end : {})", durationSec, startTime, endTime);
        }
    }
}
