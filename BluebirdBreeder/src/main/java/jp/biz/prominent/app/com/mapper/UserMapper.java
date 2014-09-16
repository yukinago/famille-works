package jp.biz.prominent.app.com.mapper;

import java.util.List;

import jp.biz.prominent.app.entity.UserEntity;

import org.apache.ibatis.annotations.Param;

/**
 * ユーザー情報のマッピングインターフェイス。
 * 
 * @author famille
 */
public interface UserMapper {

	/**
	 * テーブルが存在しない場合は作成する。
	 */
	void createTable();

	/**
	 * ユーザー情報の全レコードを取得する
	 * 
	 * @return 全ユーザー情報リスト
	 */
	List<UserEntity> selectAll();

	/**
	 * ユーザーIDをもとに、指定ユーザー情報を取得する。
	 * 
	 * @param uid ユーザーID
	 * @return 全ユーザー情報リスト
	 */
	UserEntity selectById(@Param("uid") Long uid);

	/**
	 * ユーザー情報を追加する。
	 * 
	 * @param userEntity ユーザー情報
	 * @return 更新件数
	 */
	int insert(@Param("user") UserEntity userEntity);

	/**
	 * ユーザー情報を更新する。
	 * 
	 * @param userEntity ユーザー情報
	 * @return 更新件数
	 */
	int update(@Param("user") UserEntity userEntity);

	/**
	 * ユーザーIDをもとに、ユーザー情報を削除する。
	 * 
	 * @param uid 対象ユーザーID
	 * @return 削除件数
	 */
	int deleteByUid(@Param("uid") Long uid);
}
