package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BatchHistoryRepositoryTest {

    @Autowired
    BatchHistoryRepository batchHistoryRepository;

    /**
     * 最新のバッチ履歴取得
     * ケース：正常系
     * コンディション：履歴データが存在する
     */
    @Test
    void selectLatestRecord_Success001() {

        // テスト実行
        BatchHistoryRecord result = batchHistoryRepository.selectLatestRecord();

        // テスト結果
        assertThat(result, is(notNullValue()));
        assertThat(result.getLastProcessedTweetId(), is(300));
        assertThat(result.getProcessedNum(), is(7));
        assertThat(result.getSucceeded(), is(6));
        assertThat(result.getExecutionStart(), is(java.sql.Timestamp.valueOf("2025-04-03 12:00:00")));
        assertThat(result.getExecutionEnd(), is(java.sql.Timestamp.valueOf("2025-04-03 12:00:12")));
    }

    /**
     * 最新のバッチ履歴取得
     * ケース：異常系
     * コンディション：履歴データが存在しない
     */
    @Test
    void selectLatestRecord_Error001() {

        // クリアデータ
        batchHistoryRepository.deleteAll();

        // テスト実行
        BatchHistoryRecord result = batchHistoryRepository.selectLatestRecord();

        // テスト結果
        assertThat(result, is(nullValue()));
    }
}
