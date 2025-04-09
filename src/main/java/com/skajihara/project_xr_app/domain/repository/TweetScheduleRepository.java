package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TweetScheduleRepository extends JpaRepository<ScheduledTweetRecord, Integer> {

    /**
     * 1件の予約ツイート情報を取得する
     *
     * @param scheduleId 予約ツイートID
     * @return １件のツイート情報
     */
    @Query("SELECT t FROM ScheduledTweetRecord t WHERE t.id = :scheduleId AND t.deleteFlag = 0")
    ScheduledTweetRecord selectScheduledTweet(@Param("scheduleId") int scheduleId);

    /**
     * 特定アカウントの予約ツイート情報を取得する
     *
     * @param accountId アカウントID
     * @return 特定アカウントの指定件数の予約ツイート情報
     */
    @Query("SELECT t FROM ScheduledTweetRecord t WHERE t.accountId = :accountId AND t.deleteFlag = 0 ORDER BY t.scheduledDatetime ASC")
    List<ScheduledTweetRecord> selectScheduledTweetsByAccountId(@Param("accountId") String accountId);

    /**
     * 予約日時を過ぎている予約ツイート情報を取得する
     *
     * @param lastProcessedId 最後に処理した予約ツイートID
     * @param now             現在日時
     * @return 条件に合致する予約ツイートのリスト
     */
    @Query("SELECT t FROM ScheduledTweetRecord t WHERE t.id > :lastProcessedId AND t.scheduledDatetime < :now AND t.deleteFlag = 0 ORDER BY t.scheduledDatetime ASC, t.id ASC")
    List<ScheduledTweetRecord> selectScheduledTweetsForBatch(@Param("lastProcessedId") int lastProcessedId, @Param("now") LocalDateTime now);

    /**
     * 1件の予約ツイートを登録する
     *
     * @param schedule 予約ツイート情報
     * @return 登録件数
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("INSERT INTO ScheduledTweetRecord (accountId, text, image, location, scheduledDatetime, createdDatetime, deleteFlag) " +
            "VALUES (:#{#schedule.accountId}, :#{#schedule.text}, :#{#schedule.image}, :#{#schedule.location}, :#{#schedule.scheduledDatetime}, :#{#schedule.createdDatetime}, 0)")
    int insert(@Param("schedule") ScheduledTweetRecord schedule);

    /**
     * 1件の予約ツイートを更新する
     *
     * @param scheduleId      更新対象予約ツイートID
     * @param updatedSchedule 予約ツイート更新情報
     * @return 更新件数
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE ScheduledTweetRecord t SET " +
            "t.text = :#{#updatedSchedule.text}, " +
            "t.image = :#{#updatedSchedule.image}, " +
            "t.location = :#{#updatedSchedule.location}, " +
            "t.scheduledDatetime = :#{#updatedSchedule.scheduledDatetime}, " +
            "t.deleteFlag = :#{#updatedSchedule.deleteFlag} " +
            "WHERE t.id = :scheduleId")
    int update(@Param("scheduleId") int scheduleId, @Param("updatedSchedule") ScheduledTweetRecord updatedSchedule);

    /**
     * 1件の予約ツイートを削除する
     *
     * @param scheduleId 削除対象予約ツイートID
     * @return 削除件数
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE ScheduledTweetRecord t SET t.deleteFlag = 1 WHERE t.id = :scheduleId")
    int delete(@Param("scheduleId") int scheduleId);

}
