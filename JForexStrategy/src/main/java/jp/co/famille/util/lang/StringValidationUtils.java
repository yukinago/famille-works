package jp.co.famille.util.lang;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 文字列のバリデーションに関するユーティリティ・クラス。
 * 
 * @author famille
 */
public class StringValidationUtils {
	/** パターン：符号なし整数 */
	private static Pattern PTN_UNSIGNED_INTEGER = Pattern.compile("^[0-9]+$");

	/**
	 * 入力値を符号なし整数として許容するか否かをチェックします。
	 * 
	 * @param value 入力値
	 * @return チェック結果。<br>true：許容、false：非許容
	 */
	public static boolean isUnsignedInteger(String value) {
		// 空の場合は許可する
		if (StringUtils.isEmpty(value)) {
			return true;
		}
		// パターンに一致するか否かを返却する
		return PTN_UNSIGNED_INTEGER.matcher(value).matches();
	}
}
