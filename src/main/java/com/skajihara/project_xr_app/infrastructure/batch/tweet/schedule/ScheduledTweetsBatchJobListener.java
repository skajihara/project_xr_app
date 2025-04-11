package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import com.skajihara.project_xr_app.domain.repository.BatchHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledTweetsBatchJobListener implements JobExecutionListener {

    private final BatchHistoryRepository batchHistoryRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info("start batch processing of scheduled tweets posting. jobId: {}", jobExecution.getJobId());

        try {
            // 最新の処理済みIDを取得
            BatchHistoryRecord latest = batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob");
            int latestProcessedId = (latest != null) ? latest.getLatestProcessedId() : 0;

            // 新規バッチ履歴レコードを作成（idはDBで採番）
            BatchHistoryRecord history = new BatchHistoryRecord(0, "scheduledTweetsPostingJob", latestProcessedId, 0, LocalDateTime.now().withNano(0), null, 0);

            // バッチ開始履歴登録
            batchHistoryRepository.insert(history);

            // 処理対象となる予約IDをExecutionContextに入れる（Taskletに渡すため）
            BatchHistoryRecord inserted = batchHistoryRepository.selectLatestRecord("scheduledTweetsPostingJob");
            jobExecution.getExecutionContext().putInt("batchHistoryId", inserted.getId());

            log.info("generated and registered batch history record. (latestProcessedId: {})", latestProcessedId);

        } catch (Exception e) {
            log.error("failed batch history initialize.", e);
            throw e;
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Integer batchHistoryId = jobExecution.getExecutionContext().getInt("batchHistoryId", -1);
        if (batchHistoryId == -1) {
            log.error("skip update because the history id does not exist in ExecutionContext.");
            return;
        }

        try {
            BatchHistoryRecord history = batchHistoryRepository.selectByPrimaryKey(batchHistoryId);
            history.setExecutionEnd(LocalDateTime.now().withNano(0));
            history.setSucceeded(jobExecution.getStatus() == BatchStatus.COMPLETED ? 1 : 0);
            batchHistoryRepository.update(batchHistoryId, history);

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("succeeded batch processing of scheduled tweets posting. jobId: {}", jobExecution.getJobId());
            } else {
                log.error("failed batch processing of scheduled tweets posting. jobId: {}", jobExecution.getJobId());
            }

        } catch (Exception e) {
            log.error("failed batch history update.", e);
        }
    }
}
