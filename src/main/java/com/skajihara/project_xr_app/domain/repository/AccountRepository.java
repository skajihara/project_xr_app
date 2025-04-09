package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountRecord, String> {

    /**
     * 全てのアカウント情報を取得する
     *
     * @return 全アカウント情報
     */
    @Query("SELECT a FROM AccountRecord a WHERE a.validFlag = 1 AND a.deleteFlag = 0 ORDER BY a.id DESC")
    List<AccountRecord> selectAllAccounts();

    /**
     * 1件のアカウント情報を取得する
     *
     * @param id アカウントID
     * @return 1件のアカウント情報
     */
    @Query("SELECT a FROM AccountRecord a WHERE a.id = :id AND a.validFlag = 1 AND a.deleteFlag = 0")
    AccountRecord selectAccount(@Param("id") String id);
}
