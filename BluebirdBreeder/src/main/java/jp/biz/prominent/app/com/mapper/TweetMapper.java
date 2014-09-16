package jp.biz.prominent.app.com.mapper;

import java.util.List;

import jp.biz.prominent.app.entity.TweetEntity;

import org.apache.ibatis.annotations.Param;

/**
 * ツイートテキストのマッピングインターフェイス。
 * 
 * @author famille
 */
public interface TweetMapper {

	/**
	 * テーブルが存在しない場合は作成する。
	 */
	public void createTable();

	/**
	 * ユーザーIDをもとに、ツイート情報を取得する。
	 * 
	 * @param uid ユーザーID
	 * @return ツイート情報リスト
	 */
	List<TweetEntity> selectByUid(@Param("uid") Long uid);

	/**
	 * ツィート情報を追加する。
	 * 
	 * @param tweetEntity ツィート情報
	 * @return 更新件数
	 */
	int insert(@Param("tweet") TweetEntity tweetEntity);

	/**
	 * ツィート情報を更新する。
	 * 
	 * @param tweetEntity ツィート情報
	 * @return 更新件数
	 */
	int update(@Param("tweet") TweetEntity tweetEntity);

	/**
	 * ツィートIDを更新する。
	 * 
	 * @param tweetEntity ツィート情報
	 * @return 更新件数
	 */
	int updateTweetId(@Param("tweet") TweetEntity tweetEntity);

	/**
	 * 指定ユーザーのツィート情報を削除する。
	 * 
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	int deleteByUid(@Param("uid") Long uid);

	/**
	 * ツィート情報を１件削除する。
	 * 
	 * @param tweetEntity ツィート情報
	 * @return 削除件数
	 */
	int delete(@Param("tweet") TweetEntity tweetEntity);
}
