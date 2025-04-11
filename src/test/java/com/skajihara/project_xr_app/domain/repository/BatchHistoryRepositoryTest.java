package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.BatchHistoryRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        assertThat(result.getLatestProcessedId(), is(300));
        assertThat(result.getProcessedNum(), is(7));
        assertThat(result.getSucceeded(), is(6));
        assertThat(result.getExecutionStart(), is(LocalDateTime.of(2025, 4, 3, 12, 0, 0)));
        assertThat(result.getExecutionEnd(), is(LocalDateTime.of(2025, 4, 3, 12, 0, 12)));
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

    /**
     * バッチ履歴登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void insert_Success001() {

        // テストデータ
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BatchHistoryRecord record = new BatchHistoryRecord(0, 999, 10, now.minusSeconds(15), now, 10);

        // テスト実行
        int result = batchHistoryRepository.insert(record);

        // テスト結果
        assertThat(result, is(1));

        // 登録された最新データを取得して確認
        BatchHistoryRecord inserted = batchHistoryRepository.selectLatestRecord();
        assertThat(inserted.getLatestProcessedId(), is(999));
        assertThat(inserted.getProcessedNum(), is(10));
        assertThat(inserted.getSucceeded(), is(10));
        assertThat(inserted.getExecutionStart(), is(record.getExecutionStart()));
        assertThat(inserted.getExecutionEnd(), is(record.getExecutionEnd()));
    }

    /**
     * バッチ履歴更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void update_Success001() {

        // 前提：最新レコード取得して更新対象にする
        BatchHistoryRecord original = batchHistoryRepository.selectLatestRecord();
        int id = original.getId();
        LocalDateTime now = LocalDateTime.now().withNano(0);

        // 更新内容
        BatchHistoryRecord updated = new BatchHistoryRecord(
                id,
                original.getLatestProcessedId() + 1,
                original.getProcessedNum() + 1,
                original.getExecutionStart(),
                now,
                original.getSucceeded() + 1
        );

        // テスト実行
        int result = batchHistoryRepository.update(id, updated);

        // テスト結果
        assertThat(result, is(1));

        BatchHistoryRecord after = batchHistoryRepository.selectLatestRecord();
        assertThat(after.getId(), is(id));
        assertThat(after.getLatestProcessedId(), is(updated.getLatestProcessedId()));
        assertThat(after.getProcessedNum(), is(updated.getProcessedNum()));
        assertThat(after.getExecutionEnd(), is(updated.getExecutionEnd()));
        assertThat(after.getSucceeded(), is(updated.getSucceeded()));
    }

}
