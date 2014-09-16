package jp.biz.prominent.app.com.mapper;

import java.util.List;

import jp.biz.prominent.app.entity.FollowerEntity;

import org.apache.ibatis.annotations.Param;

/**
 * フォローワー情報のマッピングインターフェイス。
 * 
 * @author famille
 */
public interface FollowerMapper {

	/**
	 * テーブルが存在しない場合は作成する。
	 */
	void createTable();

	/**
	 * ユーザーIDをもとに、指定フォローワー情報を取得する。
	 * 
	 * @param uid ユーザーID
	 * @return 全フォローワー情報リスト
	 */
	List<FollowerEntity> selectByUid(@Param("uid") Long uid);

	/**
	 * フォローワー情報を追加する。
	 * 
	 * @param followerEntity フォローワー情報
	 * @return 更新件数
	 */
	int insert(@Param("follower") FollowerEntity followerEntity);

	/**
	 * フォローワー情報を更新する。
	 * 
	 * @param followerEntity フォローワー情報
	 * @return 更新件数
	 */
	int update(@Param("follower") FollowerEntity followerEntity);

	/**
	 * 指定ユーザーのフォローワー情報を削除する。
	 * 
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	int deleteByUid(@Param("uid") Long uid);

	/**
	 * フォローワー情報を１件削除する。
	 * 
	 * @param followerEntity フォローワー情報
	 * @return 削除件数
	 */
	int delete(@Param("follower") FollowerEntity followerEntity);

	/**
	 * 既存の未Follow対象を削除する。
	 * 
	 * @param uid ユーザーID
	 * @return 削除件数
	 */
	int deleteExistUnFollow(@Param("uid") Long uid);

}
