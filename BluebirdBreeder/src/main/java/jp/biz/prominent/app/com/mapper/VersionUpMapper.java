package jp.biz.prominent.app.com.mapper;

/**
 * ツイートテキストのマッピングインターフェイス。
 * 
 * @author famille
 */
public interface VersionUpMapper {

	/**
	 * 旧ユーザーテーブルのカウントを取得する。<br>
	 * 存在する場合は「1」を返却する。
	 * 
	 * @return テーブル数
	 */
	int countOldUserTable();

	/**
	 * 旧ユーザーテーブルのカウントを取得する。<br>
	 * 存在する場合は「1」を返却する。
	 * 
	 * @return テーブル数
	 */
	int countOldFollowerTable();

	/**
	 * 旧ユーザーテーブルのカウントを取得する。<br>
	 * 存在する場合は「1」を返却する。
	 * 
	 * @return テーブル数
	 */
	int countOldTweetTable();

	/**
	 * ユーザーテーブルのマージを行う。
	 * 
	 * @return 更新レコード数
	 */
	int mergeUserTable();

	/**
	 * フォローワーテーブルのマージを行う。
	 * 
	 * @return 更新レコード数
	 */
	int mergeFollowerTable();

	/**
	 * ツイートテーブルのマージを行う。
	 * 
	 * @return 更新レコード数
	 */
	int mergeTweetTable();

	/**
	 * 旧ユーザーテーブルの削除を行う。
	 */
	void dropOldUserTable();

	/**
	 * 旧フォローワーテーブルの削除を行う。
	 */
	void dropOldFollowerTable();

	/**
	 * 旧ツイートテーブルの削除を行う。
	 */
	void dropOldTweetTable();

}
