package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BatchHistoryRepository extends JpaRepository<BatchHistoryRecord, Integer> {

    /**
     * 指定のバッチ履歴を取得する
     *
     * @param id 履歴ID
     * @return 指定のバッチ履歴
     */
    @Query("SELECT bh FROM BatchHistoryRecord bh WHERE bh.id = :id")
    BatchHistoryRecord selectByPrimaryKey(@Param("id") int id);

    /**
     * 特定ジョブ名で最新バッチ履歴を取得する
     *
     * @return 最新のバッチ履歴情報
     */
    @Query("SELECT bh FROM BatchHistoryRecord bh WHERE bh.jobName = :jobName ORDER BY executionEnd DESC LIMIT 1")
    BatchHistoryRecord selectLatestRecord(@Param("jobName") String jobName);

    /**
     * 1件のバッチ履歴を登録する
     *
     * @param history バッチ履歴
     * @return 登録件数
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("INSERT INTO BatchHistoryRecord (latestProcessedId, processedNum, executionStart, executionEnd, succeeded, jobName) " +
            "VALUES (:#{#history.latestProcessedId}, :#{#history.processedNum}, :#{#history.executionStart}, :#{#history.executionEnd}, :#{#history.succeeded}, :#{#history.jobName})")
    int insert(@Param("history") BatchHistoryRecord history);

    /**
     * 1件のバッチ履歴を更新する
     *
     * @param history バッチ履歴
     * @return 更新件数
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE BatchHistoryRecord bh SET " +
            "bh.latestProcessedId = :#{#history.latestProcessedId}, " +
            "bh.processedNum = :#{#history.processedNum}, " +
            "bh.executionStart = :#{#history.executionStart}, " +
            "bh.executionEnd = :#{#history.executionEnd}, " +
            "bh.succeeded = :#{#history.succeeded}, " +
            "bh.jobName = :#{#history.jobName} " +
            "WHERE bh.id = :id")
    int update(@Param("id") int id, @Param("history") BatchHistoryRecord history);

}
