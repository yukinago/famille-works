package jp.biz.prominent.jforex.mapper;

import jp.biz.prominent.jforex.entity.Login;

import org.apache.ibatis.annotations.Param;

/**
 * ログイン情報のマッピングインターフェイス。
 * 
 * @author famille
 */
public interface LoginMapper {

	/**
	 * テーブルが存在しない場合は作成する。
	 */
	void createTable();

	/**
	 * ログイン情報を取得する。
	 * 
	 * @return ログイン情報
	 */
	Login select();

	/**
	 * ログイン情報を追加する。
	 * 
	 * @param login ログイン情報
	 * @return 更新件数
	 */
	int insert(@Param("login") Login login);

	/**
	 * ログイン情報を削除する。
	 * 
	 * @return 削除件数
	 */
	int delete();
}
