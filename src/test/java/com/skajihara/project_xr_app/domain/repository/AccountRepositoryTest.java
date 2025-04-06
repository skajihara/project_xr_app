package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    /**
     * アカウント全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void selectAll_Accounts_Success001() {

        // テスト実行
        List<AccountRecord> accounts = accountRepository.selectAllAccounts();

        // テスト結果
        assertThat(accounts, is(notNullValue()));
        assertThat(accounts.size(), is(5));
        accounts.forEach(tweet -> {
            assertThat(tweet.getValidFlag(), is(1));
            assertThat(tweet.getDeleteFlag(), is(0));
        });
    }

    /**
     * アカウント全件取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void selectAll_Accounts_Success002() {

        // クリアデータ
        accountRepository.deleteAll();

        // テスト実行
        List<AccountRecord> accounts = accountRepository.selectAllAccounts();

        // テスト結果
        assertThat(accounts.size(), is(0));
    }

    /**
     * アカウント1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void selectAccount_Success001() {

        // テストデータ
        String accountId = "q30387";

        // テスト実行
        AccountRecord account = accountRepository.selectAccount(accountId);

        // テスト結果
        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(accountId));
        assertThat(account.getName(), is("Shingo Kajihara"));
        assertThat(account.getBio(), is("最近はVue.jsを学習中です。"));
        assertThat(account.getIcon(), is("/src/assets/icons/user/myicon.svg"));
        assertThat(account.getHeaderPhoto(), is("/src/assets/images/header/h01.jpg"));
        assertThat(account.getLocation(), is("東京都"));
        assertThat(account.getBirthday(), is(LocalDate.of(1985, 5, 23)));
        assertThat(account.getRegistered(), is(LocalDate.of(2015, 6, 15)));
        assertThat(account.getFollowing(), is(10));
        assertThat(account.getFollower(), is(15));
        assertThat(account.getValidFlag(), is(1));
        assertThat(account.getDeleteFlag(), is(0));
    }

    /**
     * アカウント1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void selectAccount_Error001() {

        // テストデータ
        String accountId = "000000";

        // テスト実行
        AccountRecord account = accountRepository.selectAccount(accountId);

        // テスト結果
        assertThat(account, is(nullValue()));
    }
}
