package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.domain.repository.TweetScheduleRepository;
import com.skajihara.project_xr_app.exception.TweetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TweetScheduleService {

    @Autowired
    private TweetScheduleRepository tweetScheduleRepository;

    /**
     * 1件の予約ツイート情報を取得する
     *
     * @param scheduleId 予約ツイートID
     * @return １件のツイート情報
     */
    public ScheduledTweetRecord getScheduledTweet(int scheduleId) {
        ScheduledTweetRecord schedule = tweetScheduleRepository.selectScheduledTweet(scheduleId);
        if (Objects.isNull(schedule)) {
            throw new TweetException("Scheduled tweet is not found with id: " + scheduleId);
        }
        return schedule;
    }

    /**
     * 特定アカウントの予約ツイート情報を取得する
     *
     * @param accountId アカウントID
     * @return 特定アカウントの指定件数の予約ツイート情報
     */
    public List<ScheduledTweetRecord> getScheduledTweetsByAccountId(String accountId) {
        return tweetScheduleRepository.selectScheduledTweetsByAccountId(accountId);
    }

    /**
     * 1件のツイートを予約する
     *
     * @param schedule 予約ツイート情報
     * @return 予約結果
     */
    public int scheduleTweet(ScheduledTweetRecord schedule) {
        int result = tweetScheduleRepository.insert(schedule);
        if (result == 0) {
            throw new TweetException("Tweet schedule register is failed.");
        }
        return result;
    }

    /**
     * 1件の予約ツイートを更新する
     *
     * @param scheduleId      更新対象予約ツイートID
     * @param updatedSchedule 予約ツイート更新情報
     * @return 更新結果
     */
    public int updateSchedule(int scheduleId, ScheduledTweetRecord updatedSchedule) {
        ScheduledTweetRecord target = tweetScheduleRepository.selectScheduledTweet(scheduleId);
        if (Objects.isNull(target)) {
            throw new TweetException("Scheduled Tweet is not found with id: " + scheduleId);
        }
        int result = tweetScheduleRepository.update(target.getId(), updatedSchedule);
        if (result == 0) {
            throw new TweetException("Tweet schedule update is failed.");
        }
        return result;
    }

    /**
     * 1件の予約ツイートをキャンセルする
     *
     * @param scheduleId キャンセル対象予約ツイートID
     * @return キャンセル結果
     */
    public int cancelSchedule(int scheduleId) {
        ScheduledTweetRecord target = tweetScheduleRepository.selectScheduledTweet(scheduleId);
        if (Objects.isNull(target)) {
            throw new TweetException("Tweet is not found with id: " + scheduleId);
        }
        int result = tweetScheduleRepository.delete(scheduleId);
        if (result == 0) {
            throw new TweetException("Tweet schedule cancel is failed.");
        }
        return result;
    }
}
