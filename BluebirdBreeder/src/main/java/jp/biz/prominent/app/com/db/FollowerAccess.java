package jp.biz.prominent.app.com.db;

import java.util.List;

import jp.biz.prominent.app.com.mapper.FollowerMapper;
import jp.biz.prominent.app.entity.FollowerEntity;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * フォロー情報のＤＢ操作を行うクラス。
 * 
 * @author famille
 */
public class FollowerAccess {

	@Inject
	private FollowerMapper followerMapper;

	/**
	 * テーブル作成
	 *
	 */
	public void createTable() {

		// フォローワー情報のテーブルを作成する
		followerMapper.createTable();
	}

	/**
	 * 指定ユーザーIDのフォローワー情報を取得する。
	 *
	 * @param uid ユーザーID
	 * @return 指定ユーザーのフォローワー情報
	 */
	public List<FollowerEntity> selectByUid(Long uid) {

		return followerMapper.selectByUid(uid);
	}

	/**
	 * 指定フォローワー情報を登録する。
	 *
	 * @param followerEntity フォローワー情報
	 * @return 登録件数
	 */
	@Transactional
	public int insert(FollowerEntity followerEntity) {

		return followerMapper.insert(followerEntity);
	}

	/**
	 * 指定フォローワー情報を更新する。
	 *
	 * @param followerEntity フォローワー情報
	 * @return 更新件数
	 */
	@Transactional
	public int update(FollowerEntity followerEntity) {

		return followerMapper.update(followerEntity);
	}

	/**
	 * 指定フォローワー情報を削除する。
	 *
	 * @param followerEntity フォローワー情報
	 * @return 削除件数
	 */
	@Transactional
	public int delete(FollowerEntity followerEntity) {

		return followerMapper.delete(followerEntity);
	}

	/**
	 * 指定ユーザーのフォローワー情報を削除する。
	 *
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	@Transactional
	public int deleteByUid(Long uid) {

		return followerMapper.deleteByUid(uid);
	}

	/**
	 * 既存の未Follow対象を削除する。
	 *
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	@Transactional
	public int deleteExistUnFollow(Long uid) {

		try {
			return followerMapper.deleteExistUnFollow(uid);
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}

		return -1;
	}
}
