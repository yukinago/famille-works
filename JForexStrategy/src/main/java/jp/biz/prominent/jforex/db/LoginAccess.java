package jp.biz.prominent.jforex.db;

import jp.biz.prominent.jforex.entity.Login;
import jp.biz.prominent.jforex.mapper.LoginMapper;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * ログイン情報にアクセスするためのクラス。
 * 
 * @author famille
 */
public class LoginAccess {

	/** ログイン情報アクセス用マッパー */
	@Inject
	private LoginMapper mapper;

	/**
	 * ログイン情報テーブルが存在しない場合は作成する。
	 */
	public void createTable() {
		mapper.createTable();
	}

	/**
	 * ログイン情報を取得する。
	 * 
	 * @return ログイン情報
	 */
	public Login select() {
		return mapper.select();
	}

	/**
	 * ログイン情報を保存する。
	 * 
	 * @param login ログイン情報
	 * @return 登録件数
	 */
	@Transactional
	public int insert(Login login) {
		return mapper.insert(login);
	}

	/**
	 * ログイン情報を削除する。
	 * 
	 * @return 削除件数
	 */
	@Transactional
	public int delete() {
		return mapper.delete();
	}
}
