package com.skajihara.project_xr_app.infrastructure.batch.tweet.schedule.step;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.domain.repository.BatchHistoryRepository;
import com.skajihara.project_xr_app.domain.repository.TweetRepository;
import com.skajihara.project_xr_app.domain.repository.TweetScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@StepScope
@Component
@AllArgsConstructor
@Slf4j
public class ScheduledTweetsPostingTasklet implements Tasklet {

    private final BatchHistoryRepository batchHistoryRepository;
    private final TweetScheduleRepository tweetScheduleRepository;
    private final TweetRepository tweetRepository;

    /**
     * 予約ツイートの送信処理の内部実装
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        log.info("start tasklet of scheduled tweets posting. ");

        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        int batchHistoryId = jobExecution.getExecutionContext().getInt("batchHistoryId", -1);

        if (batchHistoryId == -1) {
            throw new IllegalStateException("history id does not exist in ExecutionContext.");
        }

        BatchHistoryRecord batchHistory = batchHistoryRepository.selectByPrimaryKey(batchHistoryId);
        if (batchHistory == null) {
            throw new IllegalStateException("batch history does not exist.");
        }

        int latestProcessedId = batchHistory.getLatestProcessedId();
        int processedCount = 0;

        try {
            // 処理対象の予約ツイートを取得
            List<ScheduledTweetRecord> scheduledTweets = tweetScheduleRepository
                    .selectScheduledTweetsForBatch(latestProcessedId, LocalDateTime.now().withNano(0));
            log.info("the number of target is: {}", scheduledTweets.size());

            log.info("start tweets posting.");
            for (ScheduledTweetRecord scheduledTweet : scheduledTweets) {
                // TweetRecordをコンストラクタで安全生成（null回避）
                TweetRecord tweet = new TweetRecord(
                        null, // idは自動採番
                        scheduledTweet.getAccountId(),
                        scheduledTweet.getText(),
                        scheduledTweet.getImage(),
                        0, 0, 0, 0,
                        LocalDateTime.now().withNano(0),
                        scheduledTweet.getLocation(),
                        0
                );
                tweetRepository.insert(tweet);

                // 投稿した予約ツイートを削除
                scheduledTweet.setDeleteFlag(1);
                tweetScheduleRepository.update(scheduledTweet.getId(), scheduledTweet);

                log.info("processed scheduleId : {} ", scheduledTweet.getId());
                log.info("the number of process completed is : {} ", ++processedCount);

                if (scheduledTweet.getId() > latestProcessedId) {
                    latestProcessedId = scheduledTweet.getId();
                }
            }

            // 処理件数と最新IDだけ更新（endやsucceededはリスナーがやる）
            batchHistory.setProcessedNum(processedCount);
            batchHistory.setLatestProcessedId(latestProcessedId);
            batchHistoryRepository.update(batchHistoryId, batchHistory);

            log.info("end tweets posting.");

        } catch (Exception e) {
            log.error("exception occured.", e);
            throw e;
        }

        log.info("end tasklet of scheduled tweets posting. ");
        return RepeatStatus.FINISHED;
    }
}
