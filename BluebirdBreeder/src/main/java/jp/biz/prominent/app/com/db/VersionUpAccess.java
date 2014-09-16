package jp.biz.prominent.app.com.db;

import jp.biz.prominent.app.com.mapper.VersionUpMapper;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * テーブル定義のバージョンアップを行うクラス。<br>
 * TODO：暫定でver1.16→1.17のみに対応。
 * 
 * @author famille
 */
public class VersionUpAccess {

	@Inject
	private VersionUpMapper versionUpMapper;

	/**
	 * テーブルのバージョンアップを行う。
	 */
	@Transactional()
	public void versionUp() {
		// 旧バージョンテーブルの存在チェック
		boolean existOldUserTable = false;
		boolean existOldFollowerTable = false;
		boolean existOldTweetTable = false;

		// ユーザー情報が存在する場合
		if (versionUpMapper.countOldUserTable() == 1) {
			// テーブル削除フラグをONにする
			existOldUserTable = true;

			// ユーザー情報のマージ
			versionUpMapper.mergeUserTable();
		}

		// フォローワー情報
		if (versionUpMapper.countOldFollowerTable() == 1) {
			// テーブル削除フラグをONにする
			existOldFollowerTable = true;

			// フォローワー情報のマージ
			versionUpMapper.mergeFollowerTable();
		}

		// ツイート情報
		if (versionUpMapper.countOldTweetTable() == 1) {
			// テーブル削除フラグをONにする
			existOldTweetTable = true;

			// ツイート情報のマージ
			versionUpMapper.mergeTweetTable();
		}

		if (existOldUserTable) {
			// 旧ユーザーテーブルの削除
			versionUpMapper.dropOldUserTable();
		}

		if (existOldFollowerTable) {
			// 旧フォローワーテーブルの削除
			versionUpMapper.dropOldFollowerTable();
		}

		if (existOldTweetTable) {
			// 旧ツイートテーブルの削除
			versionUpMapper.dropOldTweetTable();
		}
	}
}
