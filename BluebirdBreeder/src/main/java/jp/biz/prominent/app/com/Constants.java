package jp.biz.prominent.app.com;

import java.awt.Color;

/**
 * 本アプリケーションで利用する定数を定義するインターフェイス。
 * 
 * @author famille
 */
public interface Constants {

	/** 背景色 */
	Color BG_COLOR = new Color(Integer.parseInt("C5", 16), Integer.parseInt("EF", 16), Integer.parseInt("F8", 16));

	/** 偶数行色 */
	Color EVEN_COLOR = new Color(240, 255, 255);

	/** フォロー状態：未フォロー */
	String FOLLOW_NOT = "未フォロー";

	/** フォロー状態：Local済 */
	String FOLLOW_LOCAL = "Local済";

	/** フォロー状態：Server済 */
	String FOLLOW_SERVER = "Server済";

	/** フォロー状態：禁止 */
	String FOLLOW_FORBI = "禁止";

	/** フォロー間隔：180秒 */
	int FOLLOW_SLEEP = 180;

	/** フォローゆらぎ：60秒(0-59) */
	int FOLLOW_RANDOM = 59;

	/** フラグ：ON */
	String FLG_ON = "1";
	/** フラグ：OFF */
	String FLG_OFF = "0";

	/** フォロー結果 */
	enum FollowResult {
		NORMAL,
		USER_COUNT_OVER,
		DAY_COUNT_OVER,
		TWITTER_API_LIMIT
	}

}
