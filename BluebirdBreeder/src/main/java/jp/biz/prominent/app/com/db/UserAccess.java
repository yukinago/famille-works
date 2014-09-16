package jp.biz.prominent.app.com.db;

import java.util.List;

import jp.biz.prominent.app.com.mapper.UserMapper;
import jp.biz.prominent.app.entity.UserEntity;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * ユーザー情報のＤＢ操作を行うクラス。
 * 
 * @author famille
 */
public class UserAccess {

	@Inject
	private UserMapper userMapper;

	/**
	 * テーブル作成
	 *
	 */
	public void createTable() {

		// ユーザー情報のテーブルを作成する
		userMapper.createTable();
	}

	/**
	 * 全ユーザー情報を取得する。
	 *
	 * @return 全ユーザー情報
	 */
	public List<UserEntity> selectAll() {

		return userMapper.selectAll();

	}

	/**
	 * 指定ユーザーIDのユーザー情報を取得する。
	 *
	 * @param id ユーザーID
	 * @return 指定ユーザー情報
	 */
	public UserEntity selectByUid(Long id) {

		return userMapper.selectById(id);

	}

	/**
	 * 指定ユーザー情報を登録する。
	 *
	 * @param userEntity ユーザー情報
	 * @return 登録件数
	 */
	@Transactional
	public int insert(UserEntity userEntity) {

		return userMapper.insert(userEntity);
	}

	/**
	 * 指定ユーザー情報を更新する。
	 *
	 * @param userEntity ユーザー情報
	 * @return 更新件数
	 */
	@Transactional
	public int update(UserEntity userEntity) {

		return userMapper.update(userEntity);
	}

	/**
	 * 指定ユーザー情報を削除する。
	 *
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	@Transactional
	public int deleteByUid(Long uid) {

		return userMapper.deleteByUid(uid);
	}

}
