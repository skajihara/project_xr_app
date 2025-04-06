package com.skajihara.project_xr_app.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import com.skajihara.project_xr_app.exception.TweetException;
import com.skajihara.project_xr_app.infrastructure.controller.model.TweetModel;
import com.skajihara.project_xr_app.infrastructure.service.TweetService;
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
import java.util.Arrays;
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
@Import(TweetControllerTest.MockConfig.class)
class TweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TweetService tweetService;

    private static final String BASE_PATH = "/api/tweet";

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TweetService tweetService() {
            return mock(TweetService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(tweetService);
    }

    /**
     * ツイート1件取得
     * ケース：正常系
     * コンディション：対象データあり
     */
    @Test
    void getTweet_Success001() throws Exception {
        TweetRecord tweet = createRecord(1, "user1", "Hello", null);
        when(tweetService.getTweet(1)).thenReturn(tweet);

        MockHttpServletRequestBuilder req = get(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel.class);
        assertModel(actual, tweet);
        verify(tweetService, times(1)).getTweet(1);
    }

    /**
     * ツイート1件取得
     * ケース：異常系
     * コンディション：対象データなし
     */
    @Test
    void getTweet_Error001() throws Exception {
        when(tweetService.getTweet(999)).thenThrow(new TweetException("not found"));
        mockMvc.perform(get(BASE_PATH + "/999")).andExpect(status().isNotFound());
        verify(tweetService, times(1)).getTweet(999);
    }

    /**
     * ツイート全件取得
     * ケース：正常系
     * コンディション：データあり
     */
    @Test
    void getAllTweets_Success001() throws Exception {
        List<TweetRecord> tweets = Arrays.asList(
                createRecord(1, "user1", "Tweet1", null),
                createRecord(2, "user2", "Tweet2", null)
        );
        when(tweetService.getAllTweets()).thenReturn(tweets);

        MockHttpServletRequestBuilder req = get(BASE_PATH);
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);

        assertThat(actual.length, is(2));
        assertModel(actual[0], tweets.get(0));
        assertModel(actual[1], tweets.get(1));
        verify(tweetService, times(1)).getAllTweets();
    }

    /**
     * ツイート全件取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void getAllTweets_Success002() throws Exception {
        when(tweetService.getAllTweets()).thenReturn(Collections.emptyList());
        MockHttpServletRequestBuilder req = get(BASE_PATH);
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);
        assertThat(actual.length, is(0));
        verify(tweetService, times(1)).getAllTweets();
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 5件指定 2件取得
     */
    @Test
    void getRecentTweets_Success001() throws Exception {
        List<TweetRecord> tweets = Arrays.asList(
                createRecord(1, "user1", "A", null),
                createRecord(2, "user2", "B", null)
        );
        when(tweetService.getRecentTweets(5)).thenReturn(tweets);

        MockHttpServletRequestBuilder req = get(BASE_PATH + "/recent?limit=5");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);
        assertThat(actual.length, is(2));
        assertModel(actual[0], tweets.get(0));
        assertModel(actual[1], tweets.get(1));
        verify(tweetService, times(1)).getRecentTweets(5);
    }

    /**
     * 最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void getRecentTweets_Success002() throws Exception {
        when(tweetService.getRecentTweets(5)).thenReturn(Collections.emptyList());
        MockHttpServletRequestBuilder req = get(BASE_PATH + "/recent?limit=5");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);
        assertThat(actual.length, is(0));
        verify(tweetService, times(1)).getRecentTweets(5);
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データあり 5件指定 2件取得
     */
    @Test
    void getRecentTweetsByAccountId_Success001() throws Exception {
        List<TweetRecord> tweets = Arrays.asList(
                createRecord(1, "user1", "Hello1", null),
                createRecord(2, "user1", "Hello2", null)
        );
        when(tweetService.getTweetsByAccountId("user1", 5)).thenReturn(tweets);

        MockHttpServletRequestBuilder req = get(BASE_PATH + "/account/user1?limit=5");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);
        assertThat(actual.length, is(2));
        assertModel(actual[0], tweets.get(0));
        assertModel(actual[1], tweets.get(1));
        verify(tweetService, times(1)).getTweetsByAccountId("user1", 5);
    }

    /**
     * 特定アカウントの最新ツイート指定件数取得
     * ケース：正常系
     * コンディション：データなし
     */
    @Test
    void getRecentTweetsByAccountId_Success002() throws Exception {
        when(tweetService.getTweetsByAccountId("user1", 5)).thenReturn(Collections.emptyList());
        MockHttpServletRequestBuilder req = get(BASE_PATH + "/account/user1?limit=5");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        TweetModel[] actual = objectMapper.readValue(result.getResponse().getContentAsString(), TweetModel[].class);
        assertThat(actual.length, is(0));
        verify(tweetService, times(1)).getTweetsByAccountId("user1", 5);
    }

    /**
     * ツイート登録
     * ケース：正常系
     * コンディション：登録成功
     */
    @Test
    void insert_Success001() throws Exception {
        TweetModel model = new TweetModel(0, "user1", "post", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);
        when(tweetService.postTweet(any())).thenReturn(1);

        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート投稿成功"));
        verify(tweetService, times(1)).postTweet(any());
    }

    /**
     * ツイート登録
     * ケース：異常系
     * コンディション：登録失敗
     */
    @Test
    void insert_Error001() throws Exception {
        TweetModel model = new TweetModel(0, "user1", "post", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);
        when(tweetService.postTweet(any())).thenReturn(0);

        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート投稿失敗"));
        verify(tweetService, times(1)).postTweet(any());
    }

    @Test
    void insert_Error002() throws Exception {

        TweetModel model = new TweetModel();

        MockHttpServletRequestBuilder req = post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    /**
     * ツイート更新
     * ケース：正常系
     * コンディション：更新成功
     */
    @Test
    void update_Success001() throws Exception {
        TweetModel model = new TweetModel(1, "user1", "updated", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);
        when(tweetService.updateTweet(eq(1), any())).thenReturn(1);

        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート更新成功"));
        verify(tweetService, times(1)).updateTweet(eq(1), any());
    }

    /**
     * ツイート更新
     * ケース：異常系
     * コンディション：更新失敗
     */
    @Test
    void update_Error001() throws Exception {
        TweetModel model = new TweetModel(1, "user1", "fail", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);
        when(tweetService.updateTweet(eq(1), any())).thenReturn(0);

        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート更新失敗"));
        verify(tweetService, times(1)).updateTweet(eq(1), any());
    }

    @Test
    void update_Error002() throws Exception {
        TweetModel model = new TweetModel(-1, "user1", "test", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);

        MockHttpServletRequestBuilder req = put(BASE_PATH + "/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    @Test
    void update_Error003() throws Exception {

        TweetModel model = new TweetModel();

        MockHttpServletRequestBuilder req = put(BASE_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    /**
     * ツイート削除
     * ケース：正常系
     * コンディション：削除成功
     */
    @Test
    void delete_Success001() throws Exception {
        when(tweetService.deleteTweet(1)).thenReturn(1);

        MockHttpServletRequestBuilder req = delete(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート削除成功"));
        verify(tweetService, times(1)).deleteTweet(1);
    }

    /**
     * ツイート削除
     * ケース：異常系
     * コンディション：削除失敗
     */
    @Test
    void delete_Error001() throws Exception {
        when(tweetService.deleteTweet(1)).thenReturn(0);

        MockHttpServletRequestBuilder req = delete(BASE_PATH + "/1");
        MvcResult result = mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();
        Map response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("message"), is("ツイート削除失敗"));
        verify(tweetService, times(1)).deleteTweet(1);
    }

    @Test
    void delete_Error002() throws Exception {
        TweetModel model = new TweetModel(-1, "user1", "test", null, 0, 0, 0, 0,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);

        MockHttpServletRequestBuilder req = delete(BASE_PATH + "/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model));

        mockMvc.perform(req).andExpect(status().isBadRequest());
    }

    // モデルの項目の詳細なバリデーションテストは後回し（登録・更新）

    private TweetRecord createRecord(int id, String accountId, String text, String image) {
        return new TweetRecord(id, accountId, text, image, 10, 5, 3, 100,
                LocalDateTime.of(2024, 4, 1, 12, 0), "Tokyo", 0);
    }

    private void assertModel(TweetModel actual, TweetRecord expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getAccountId(), is(expected.getAccountId()));
        assertThat(actual.getText(), is(expected.getText()));
        assertThat(actual.getImage(), is(expected.getImage()));
        assertThat(actual.getLikes(), is(expected.getLikes()));
        assertThat(actual.getRetweets(), is(expected.getRetweets()));
        assertThat(actual.getReplies(), is(expected.getReplies()));
        assertThat(actual.getViews(), is(expected.getViews()));
        assertThat(actual.getDatetime(), is(expected.getDatetime()));
        assertThat(actual.getLocation(), is(expected.getLocation()));
        assertThat(actual.getDeleteFlag(), is(expected.getDeleteFlag()));
    }
}
