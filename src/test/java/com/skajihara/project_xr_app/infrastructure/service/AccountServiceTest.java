package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.domain.repository.AccountRepository;
import com.skajihara.project_xr_app.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    /**
     * アカウント全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getAllAccounts_Success001() {

        // モックデータ
        List<AccountRecord> mockData = Arrays.asList(
                new AccountRecord("id1", "name1", "bio1", "icon1", "header1", "Tokyo",
                        LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), 10, 20, 1, 0),
                new AccountRecord("id2", "name2", "bio2", "icon2", "header2", "Osaka",
                        LocalDate.of(1991, 2, 2), LocalDate.of(2021, 2, 2), 15, 25, 1, 0)
        );

        // モック設定
        when(accountRepository.selectAllAccounts()).thenReturn(mockData);

        // テスト実行
        List<AccountRecord> result = accountService.getAllAccounts();

        // テスト結果
        assertThat(result.size(), is(2));
        assertAccount(result.get(0), mockData.get(0));
        assertAccount(result.get(1), mockData.get(1));
        verify(accountRepository, times(1)).selectAllAccounts();
    }

    /**
     * アカウント1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getAccount_Success001() {

        // モックデータ
        AccountRecord mockAccount = new AccountRecord("id1", "name1", "bio1", "icon1", "header1", "Tokyo",
                LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), 10, 20, 1, 0);

        // モック設定
        when(accountRepository.selectAccount("id1")).thenReturn(mockAccount);

        // テスト実行
        AccountRecord result = accountService.getAccount("id1");

        // テスト結果
        assertAccount(result, mockAccount);
        verify(accountRepository).selectAccount("id1");
    }

    /**
     * アカウント1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getAccount_Error001() {

        // モック設定
        when(accountRepository.selectAccount("notfound")).thenReturn(null);

        // テスト実行・テスト結果
        assertThrows(NotFoundException.class, () -> accountService.getAccount("notfound"));
        verify(accountRepository).selectAccount("notfound");
    }

    private void assertAccount(AccountRecord actual, AccountRecord expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getBio(), is(expected.getBio()));
        assertThat(actual.getIcon(), is(expected.getIcon()));
        assertThat(actual.getHeaderPhoto(), is(expected.getHeaderPhoto()));
        assertThat(actual.getLocation(), is(expected.getLocation()));
        assertThat(actual.getBirthday(), is(expected.getBirthday()));
        assertThat(actual.getRegistered(), is(expected.getRegistered()));
        assertThat(actual.getFollowing(), is(expected.getFollowing()));
        assertThat(actual.getFollower(), is(expected.getFollower()));
        assertThat(actual.getValidFlag(), is(expected.getValidFlag()));
        assertThat(actual.getDeleteFlag(), is(expected.getDeleteFlag()));
    }
}
