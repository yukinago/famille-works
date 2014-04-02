package jp.co.famille.util.swing;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * 許容値を設定することができるテキストフィールド。
 * 
 * @author famille
 */
public class JValidationTextField extends JTextField {

	/** 許容する値のタイプ */
	public enum ValueType {
		/** 半角 */
		Hankaku,
		/** 半角英数字 */
		HankakuEisuuji,
		/** 半角英字 */
		HankakuEiji,
		/** 全角 */
		Zenkaku,
		/** 全角英数字 */
		ZenkakuEisuuji,
		/** 全角英字 */
		ZenkakEiji,
		/** 符号有り整数 */
		SignedInteger,
		/** 符号有り小数 */
		SignedDouble,
		/** 符号なし整数 */
		UnsignedInteger,
		/** 符号なし小数 */
		UnsignedDouble
	}

	private ValueType type;

	public JValidationTextField(ValueType type) {
		this(type, null, null, 0);
	}

	public JValidationTextField(ValueType type, int column) {
		this(type, null, null, column);
	}

	public JValidationTextField(ValueType type, String name) {
		this(type, null, name, 0);
	}

	public JValidationTextField(ValueType type, String name, int column) {
		this(type, null, name, column);
	}

	public JValidationTextField(ValueType type, Document doc, String name, int column) {
		// 親クラスの初期化をそのまま流用する
		super(doc, name, column);
		// 値タイプを保存する
		this.type = type;

		switch (type) {
			case SignedInteger:
			case SignedDouble:
			case UnsignedInteger:
			case UnsignedDouble:
				// 数値タイプの場合
				// 右寄せとする
				setHorizontalAlignment(JTextField.RIGHT);
				break;
			default:
				// その他タイプの場合
				// 左寄せとする
				setHorizontalAlignment(JTextField.LEFT);
				break;
		}
	}

}
