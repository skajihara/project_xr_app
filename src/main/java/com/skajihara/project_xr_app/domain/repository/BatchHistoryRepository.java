package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchHistoryRepository extends JpaRepository<BatchHistoryRecord, Integer> {

    /**
     * 最新のバッチ履歴を取得する
     *
     * @return 最新のバッチ履歴情報
     */
    @Query("SELECT bh FROM BatchHistoryRecord bh ORDER BY id DESC LIMIT 1")
    BatchHistoryRecord selectLatestRecord();

}
