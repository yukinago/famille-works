package jp.co.famille.util.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * Swingの確認ダイアログに関するユーティリティ・クラス。
 * 
 * @author famille
 */
public class ShowDialogUtil {

	/** ボタンの種類 */
	public enum Button {
		/** OKボタン */
		OK,
		/** キャンセルボタン */
		CANCEL
	}

	/** ボタンラベル（OK） */
	private static final String OK_BUTTON = "OK";
	/** ボタンラベル（キャンセル） */
	private static final String CANCEL_BUTTON = "キャンセル";

	/** 情報ダイアログ選択肢 */
	private static final String[] INFO_ITEMS = new String[] { OK_BUTTON };
	/** 確認ダイアログ選択肢 */
	private static final String[] CONFIRM_ITEMS = new String[] { OK_BUTTON, CANCEL_BUTTON };

	/** ボタン種類とボタンラベルのマッピング */
	private static final Map<Button, String> BUTTON_MAP = new HashMap<Button, String>();
	static {
		BUTTON_MAP.put(Button.OK, OK_BUTTON);
		BUTTON_MAP.put(Button.CANCEL, CANCEL_BUTTON);
	}

	/**
	 * 確認ダイアログを表示する。
	 *
	 * @param message メッセージ
	 * @param defaultButton デフォルト選択するボタン
	 * @return 選択結果
	 */
	public static int info(String message) {
		return JOptionPane.showOptionDialog(
				null,
				message,
				"情報",
				JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				INFO_ITEMS,
				OK_BUTTON);
	}

	/**
	 * 確認ダイアログを表示する。<br>
	 * OKボタンをデフォルト選択とする。
	 *
	 * @param message メッセージ
	 * @return 選択結果
	 */
	public static int confirm(String message) {
		return confirm(message, Button.OK);
	}

	/**
	 * 確認ダイアログを表示する。
	 *
	 * @param message メッセージ
	 * @param defaultButton デフォルト選択するボタン
	 * @return 選択結果
	 */
	public static int confirm(String message, Button defaultButton) {
		return JOptionPane.showOptionDialog(
				null,
				message,
				"警告",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				CONFIRM_ITEMS,
				BUTTON_MAP.get(defaultButton));
	}
}
