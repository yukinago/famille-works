package jp.biz.prominent.app.com;

import lombok.Getter;

/**
 * ツールの各バージョンごとの個別設定を管理するクラス。
 * 
 * @author famille
 */
public class ToolInformation {
	/** ツールバージョン */
	public static enum Version {
		/** 通常版 */
		NORMAL,
		/** ライト版 */
		LITE
	}

	@Getter
	/** タイトル */
	private static String title;
	@Getter
	/** DBファイル */
	private static String dbFile;
	@Getter
	/** 最大ユーザー数 */
	private static int maxCount;
	@Getter
	/** ライトバージョン時制限項目のエディッタブル設定 */
	private static boolean edittable;
	@Getter
	/** アカウント数上限を超えて登録可能フラグ */
	private static boolean noLimit;
	@Getter
	private static boolean liteVersion;

	/**
	 * ツールのバージョンを設定し、<br>
	 * バージョンごとの個別設定を行う。
	 * 
	 * @param ver ツールバージョン
	 */
	public static void setVersion(Version ver) {

		switch (ver) {
			case NORMAL: // 通常版

				title = "Bluebird Breeder";
				dbFile = "userdata.sqlite3";
				maxCount = 10;
				edittable = true;
				noLimit = true;
				liteVersion = false;

				break;

			case LITE: // ライト版

				title = "Bluebird Breeder Lite";
				dbFile = "userdata_lite.sqlite3";
				maxCount = 1;
				edittable = false;
				noLimit = false;
				liteVersion = true;

				break;

			default:
				break;
		}
	}
}
