package com.skajihara.project_xr_app.domain.repository;

import com.skajihara.project_xr_app.domain.entity.record.TweetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<TweetRecord, Integer> {

    /**
     * 対象1件のツイート情報を取得する
     *
     * @param tweetId 取得対象件数
     * @return １件のツイート情報
     */
    @Query("SELECT t FROM TweetRecord t WHERE t.id = :tweetId AND t.deleteFlag = 0")
    TweetRecord selectTweet(@Param("tweetId") int tweetId);

    /**
     * 全てのツイート情報を取得する
     *
     * @return 全ツイート情報
     */
    @Query("SELECT t FROM TweetRecord t WHERE t.deleteFlag = 0 ORDER BY t.datetime DESC")
    List<TweetRecord> selectAllTweets();

    /**
     * 最新N件のツイート情報を取得する
     *
     * @param limit 取得対象件数
     * @return 全ツイート
     */
    @Query("SELECT t FROM TweetRecord t WHERE t.deleteFlag = 0 ORDER BY t.datetime DESC LIMIT :limit")
    List<TweetRecord> selectRecentTweets(@Param("limit") int limit);

    /**
     * 特定アカウントの最新N件のツイートを取得する
     *
     * @param accountId アカウントID
     * @param limit     取得件数
     * @return 全ツイート
     */
    @Query("SELECT t FROM TweetRecord t WHERE t.accountId = :accountId AND t.deleteFlag = 0 ORDER BY t.datetime DESC LIMIT :limit")
    List<TweetRecord> selectRecentTweetsByAccountId(@Param("accountId") String accountId, @Param("limit") int limit);

    /**
     * 1件のツイート情報を登録する
     *
     * @param tweet ツイート情報
     * @return 登録件数
     */
    @Modifying
    @Transactional
    @Query("INSERT INTO TweetRecord (accountId, text, image, likes, retweets, replies, views, datetime, location, deleteFlag) " +
            "VALUES (:#{#tweet.accountId}, :#{#tweet.text}, :#{#tweet.image}, :#{#tweet.likes}, " +
            ":#{#tweet.retweets}, :#{#tweet.replies}, :#{#tweet.views}, :#{#tweet.datetime}, :#{#tweet.location}, 0)")
    int insert(@Param("tweet") TweetRecord tweet);

    /**
     * 1件のツイート情報を更新する
     *
     * @param tweetId ツイートID
     * @param tweet   ツイート情報
     * @return 更新件数
     */
    @Modifying
    @Transactional
    @Query("UPDATE TweetRecord t SET " +
            "t.text = :#{#tweet.text}, " +
            "t.image = :#{#tweet.image}, " +
            "t.likes = :#{#tweet.likes}, " +
            "t.retweets = :#{#tweet.retweets}, " +
            "t.replies = :#{#tweet.replies}, " +
            "t.views = :#{#tweet.views}, " +
            "t.location = :#{#tweet.location}, " +
            "t.deleteFlag = :#{#tweet.deleteFlag} " +
            "WHERE t.id = :tweetId")
    int update(@Param("tweetId") int tweetId, @Param("tweet") TweetRecord tweet);

    /**
     * 1件のツイート情報を削除する
     *
     * @param tweetId ツイートID
     * @return 削除件数
     */
    @Modifying
    @Transactional
    @Query("UPDATE TweetRecord t SET t.deleteFlag = 1 WHERE t.id = :tweetId")
    int delete(@Param("tweetId") int tweetId);

}
