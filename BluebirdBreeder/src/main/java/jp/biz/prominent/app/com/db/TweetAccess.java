package jp.biz.prominent.app.com.db;

import java.util.List;

import jp.biz.prominent.app.com.mapper.TweetMapper;
import jp.biz.prominent.app.entity.TweetEntity;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * ユーザー情報のＤＢ操作を行うクラス。
 * 
 * @author famille
 */
public class TweetAccess {

	@Inject
	private TweetMapper tweetMapper;

	/**
	 * テーブル作成
	 *
	 */
	public void createTable() {

		// ツィート情報のテーブルを作成する
		tweetMapper.createTable();
	}

	/**
	 * 指定ユーザーIDのツィート情報を取得する。
	 *
	 * @param uid ユーザーID
	 * @return ツィート情報リスト
	 */
	public List<TweetEntity> selectByUid(Long uid) {

		return tweetMapper.selectByUid(uid);
	}

	/**
	 * 指定ツィート情報を登録する。
	 *
	 * @param tweetEntity ツィート情報
	 * @return 登録件数
	 */
	@Transactional
	public int insert(TweetEntity tweetEntity) {

		return tweetMapper.insert(tweetEntity);
	}

	/**
	 * 指定ツィート情報を更新する。
	 *
	 * @param tweetEntity ツィート情報
	 * @return 更新件数
	 */
	@Transactional
	public int update(TweetEntity tweetEntity) {

		return tweetMapper.update(tweetEntity);
	}

	/**
	 * 指定ツィート番号情報を更新する。
	 *
	 * @param tweetEntity ツィート情報
	 * @return 更新件数
	 */
	@Transactional
	public int updateTweetId(TweetEntity tweetEntity) {

		return tweetMapper.updateTweetId(tweetEntity);
	}

	/**
	 * 指定ツィート情報を削除する。
	 *
	 * @param tweetEntity ツィート情報
	 * @return 削除件数
	 */
	@Transactional
	public int delete(TweetEntity tweetEntity) {

		return tweetMapper.delete(tweetEntity);
	}

	/**
	 * 指定ユーザーのツィート情報を削除する。
	 *
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	@Transactional
	public int deleteByUid(Long uid) {

		return tweetMapper.deleteByUid(uid);
	}
}
