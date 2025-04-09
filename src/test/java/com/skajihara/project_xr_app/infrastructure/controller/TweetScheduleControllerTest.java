package com.skajihara.project_xr_app.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skajihara.project_xr_app.domain.entity.record.ScheduledTweetRecord;
import com.skajihara.project_xr_app.exception.TweetException;
import com.skajihara.project_xr_app.infrastructure.controller.model.ScheduledTweetModel;
import com.skajihara.project_xr_app.infrastructure.service.TweetScheduleService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(TweetScheduleControllerTest.MockConfig.class)
class TweetScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TweetScheduleService tweetScheduleService;

    private static final String BASE_PATH = "/api/schedule";

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TweetScheduleService tweetScheduleService() {
            return mock(TweetScheduleService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(tweetScheduleService);
    }

    /**
     * 予約ツイート取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getScheduledTweet_Success001() throws Exception {

        // モックデータ
        ScheduledTweetRecord record = createRecord(1, "user1", "予約テスト");

        // モック設定
        when(tweetScheduleService.getScheduledTweet(1)).thenReturn(record);

        // テスト実行
        MockHttpServletRequestBuilder req = get(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        ScheduledTweetModel actual = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduledTweetModel.class);
        assertModel(actual, record);
        verify(tweetScheduleService, times(1)).getScheduledTweet(1);
    }

    /**
     * 予約ツイート取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getScheduledTweet_Error001() throws Exception {

        // モック設定
        when(tweetScheduleService.getScheduledTweet(999)).thenThrow(new TweetException("Not found"));

        // テスト実行
        mockMvc.perform(get(BASE_PATH + "/999")).andExpect(status().isNotFound());

        // テスト結果
        verify(tweetScheduleService, times(1)).getScheduledTweet(999);
    }

    /**
     * アカウント別予約ツイート取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getScheduledTweetsByAccountId_Success001() throws Exception {

        // モックデータ
        List<ScheduledTweetRecord> list = List.of(
                createRecord(1, "user1", "予約1"),
                createRecord(2, "user1", "予約2")
        );

        // モック設定
        when(tweetScheduleService.getScheduledTweetsByAccountId("user1")).thenReturn(list);

        // テスト実行
        MvcResult result = mockMvc.perform(get(BASE_PATH + "/account/user1"))
                .andExpect(status().isOk()).andReturn();

        // テスト結果
        ScheduledTweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduledTweetModel[].class);
        assertThat(actual.length, is(2));
        assertModel(actual[0], list.get(0));
        assertModel(actual[1], list.get(1));
        verify(tweetScheduleService, times(1)).getScheduledTweetsByAccountId("user1");
    }

    /**
     * アカウント別予約ツイート取得
     * ケース：正常系
     * コンディション：対象データなし
     */
    @Test
    void getScheduledTweetsByAccountId_Success002() throws Exception {

        // モック設定
        when(tweetScheduleService.getScheduledTweetsByAccountId("empty_user")).thenReturn(Collections.emptyList());

        // テスト実行
        mockMvc.perform(get(BASE_PATH + "/account/empty_user")).andExpect(status().isOk());

        // テスト結果
        verify(tweetScheduleService, times(1)).getScheduledTweetsByAccountId("empty_user");
    }

    /**
     * ツイート予約登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void postScheduledTweet_Success001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel(0, "user1", "予約テスト", "/img.jpg", "Tokyo",
                LocalDateTime.of(2025, 4, 10, 12, 0), LocalDateTime.of(2025, 4, 9, 12, 0), 0);

        // モック設定
        when(tweetScheduleService.scheduleTweet(any())).thenReturn(1);

        // テスト実行
        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("ツイート予約成功"));
        verify(tweetScheduleService, times(1)).scheduleTweet(any());
    }

    /**
     * ツイート予約登録
     * ケース：異常系
     * コンディション：登録失敗
     */
    @Test
    void postScheduledTweet_Error001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel(0, "user1", "失敗", "/img.jpg", "Tokyo",
                LocalDateTime.of(2025, 4, 10, 12, 0), LocalDateTime.of(2025, 4, 9, 12, 0), 0);

        // モック設定
        when(tweetScheduleService.scheduleTweet(any())).thenReturn(0);

        // テスト実行
        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("ツイート予約失敗"));
        verify(tweetScheduleService, times(1)).scheduleTweet(any());
    }

    /**
     * ツイート予約登録
     * ケース：異常系
     * コンディション：バリデーションエラー（null）
     */
    @Test
    void postScheduledTweet_ValidationError001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel();

        // テスト実行・テスト結果
        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    /**
     * ツイート予約更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void updateScheduledTweet_Success001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel(1, "user1", "更新テキスト", "/img.jpg", "Tokyo",
                LocalDateTime.of(2025, 4, 11, 10, 0), LocalDateTime.of(2025, 4, 9, 12, 0), 0);

        // モック設定
        when(tweetScheduleService.updateSchedule(eq(1), any())).thenReturn(1);

        // テスト実行
        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("予約更新成功"));
        verify(tweetScheduleService, times(1)).updateSchedule(eq(1), any());
    }

    /**
     * ツイート予約更新
     * ケース：異常系
     * コンディション：更新失敗
     */
    @Test
    void updateScheduledTweet_Error001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel(1, "user1", "失敗テキスト", "/img.jpg", "Tokyo",
                LocalDateTime.of(2025, 4, 11, 10, 0), LocalDateTime.of(2025, 4, 9, 12, 0), 0);

        // モック設定
        when(tweetScheduleService.updateSchedule(eq(1), any())).thenReturn(0);

        // テスト実行
        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("予約更新失敗"));
        verify(tweetScheduleService, times(1)).updateSchedule(eq(1), any());
    }

    /**
     * ツイート予約更新
     * ケース：異常系
     * コンディション：バリデーションエラー（null）
     */
    @Test
    void updateScheduledTweet_ValidationError001() throws Exception {

        // モックデータ
        ScheduledTweetModel model = new ScheduledTweetModel();

        // テスト実行・テスト結果
        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));
        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    /**
     * ツイート予約削除
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void deleteScheduledTweet_Success001() throws Exception {

        // モック設定
        when(tweetScheduleService.cancelSchedule(1)).thenReturn(1);

        // テスト実行
        MockHttpServletRequestBuilder req = delete(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("予約キャンセル成功"));
        verify(tweetScheduleService, times(1)).cancelSchedule(1);
    }

    /**
     * ツイート予約削除
     * ケース：異常系
     * コンディション：削除失敗
     */
    @Test
    void deleteScheduledTweet_Error001() throws Exception {

        // モック設定
        when(tweetScheduleService.cancelSchedule(1)).thenReturn(0);

        // テスト実行
        MockHttpServletRequestBuilder req = delete(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();

        // テスト結果
        Map<String, String> res = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(res.get("message"), is("予約キャンセル失敗"));
        verify(tweetScheduleService, times(1)).cancelSchedule(1);
    }

    /**
     * ツイート予約削除
     * ケース：異常系
     * コンディション：バリデーションエラー（id）
     */
    @Test
    void deleteScheduledTweet_ValidationError001() throws Exception {

        // テスト実行
        mockMvc.perform(delete(BASE_PATH + "/-1")).andExpect(status().isBadRequest());
    }

    private ScheduledTweetRecord createRecord(int id, String accountId, String text) {
        return new ScheduledTweetRecord(id, accountId, text, "/img.jpg", "Tokyo",
                LocalDateTime.of(2025, 4, 8, 12, 0),
                LocalDateTime.of(2025, 4, 1, 10, 0), 0);
    }

    private void assertModel(ScheduledTweetModel actual, ScheduledTweetRecord expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getAccountId(), is(expected.getAccountId()));
        assertThat(actual.getText(), is(expected.getText()));
        assertThat(actual.getImage(), is(expected.getImage()));
        assertThat(actual.getLocation(), is(expected.getLocation()));
        assertThat(actual.getScheduledDatetime(), is(expected.getScheduledDatetime()));
        assertThat(actual.getCreatedDatetime(), is(expected.getCreatedDatetime()));
        assertThat(actual.getDeleteFlag(), is(expected.getDeleteFlag()));
    }
}
