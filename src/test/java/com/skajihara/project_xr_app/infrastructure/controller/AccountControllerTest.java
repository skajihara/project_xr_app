package com.skajihara.project_xr_app.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.exception.NotFoundException;
import com.skajihara.project_xr_app.infrastructure.controller.model.AccountModel;
import com.skajihara.project_xr_app.infrastructure.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(AccountControllerTest.MockConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AccountService accountService() {
            return mock(AccountService.class);
        }
    }

    // APIパス
    private static final String BASE_PATH = "/api/account";

    @BeforeEach
    void setUp() {
        reset(accountService);
    }

    /**
     * アカウント全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getAllAccounts_Success001() throws Exception {

        // モックデータ
        AccountRecord rec1 = createRecord("id1", "name1", "bio1", "icon1", "header1", "Tokyo",
                LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), 10, 20, 1, 0);
        AccountRecord rec2 = createRecord("id2", "name2", "bio2", "icon2", "header2", "Osaka",
                LocalDate.of(1991, 2, 2), LocalDate.of(2021, 2, 2), 15, 25, 1, 0);
        List<AccountRecord> mockRecords = Arrays.asList(rec1, rec2);

        // モック設定
        when(accountService.getAllAccounts()).thenReturn(mockRecords);

        // テスト実行
        MockHttpServletRequestBuilder req = get(BASE_PATH).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        String json = result.getResponse().getContentAsString();
        AccountModel[] actual = objectMapper.readValue(json, AccountModel[].class);

        assertThat(actual.length, is(2));
        assertModel(actual[0], rec1);
        assertModel(actual[1], rec2);

        verify(accountService, times(1)).getAllAccounts();
    }


    /**
     * アカウント全件取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void getAllAccounts_Success002() throws Exception {

        // モック設定
        when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        // テスト実行
        MockHttpServletRequestBuilder req = get(BASE_PATH).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        String json = result.getResponse().getContentAsString();
        AccountModel[] actual = objectMapper.readValue(json, AccountModel[].class);

        assertThat(actual.length, is(0));
        verify(accountService, times(1)).getAllAccounts();
    }

    /**
     * アカウント1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getAccount_Success001() throws Exception {

        // モックデータ
        AccountRecord mockRecord = createRecord("id1", "name1", "bio", "icon", "header", "Tokyo",
                LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), 10, 20, 1, 0);

        // モック設定
        when(accountService.getAccount("id1")).thenReturn(mockRecord);

        // テスト実行
        MockHttpServletRequestBuilder req = get(BASE_PATH + "/id1").contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        String json = result.getResponse().getContentAsString();
        AccountModel actual = objectMapper.readValue(json, AccountModel.class);

        assertModel(actual, mockRecord);
        verify(accountService, times(1)).getAccount("id1");
    }

    /**
     * アカウント1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getAccount_Error001() throws Exception {

        // モック設定
        when(accountService.getAccount("notfound")).thenThrow(new NotFoundException("Account id: notfound is not found"));

        // テスト実行
        MockHttpServletRequestBuilder req = get(BASE_PATH + "/notfound").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(req).andExpect(status().isNotFound());

        // テスト結果
        verify(accountService, times(1)).getAccount("notfound");
    }

    private AccountRecord createRecord(String id, String name, String bio, String icon, String headerPhoto,
                                       String location, LocalDate birthday, LocalDate registered,
                                       int following, int follower, int validFlag, int deleteFlag) {
        return new AccountRecord(id, name, bio, icon, headerPhoto, location, birthday, registered,
                following, follower, validFlag, deleteFlag);
    }

    private void assertModel(AccountModel actual, AccountRecord expected) {
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
