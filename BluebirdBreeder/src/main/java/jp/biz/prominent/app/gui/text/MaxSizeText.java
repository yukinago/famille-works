package jp.biz.prominent.app.gui.text;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import lombok.Getter;
import lombok.Setter;

public class MaxSizeText extends JTextField {

	@Getter
	@Setter
	private int maxLength;

	/**
	 * 最大桁制限付きのテキストボックスを生成する。
	 * 
	 * @param maxlength 最大桁
	 */
	@SuppressWarnings("serial")
	public MaxSizeText(int maxlength) {

		setMaxLength(maxlength);

		setDocument(new PlainDocument() {
			@Override
			public void insertString(final int offset, final String str, final AttributeSet attributes) throws BadLocationException {
				if (this.getLength() + str.length() <= getMaxLength()) {
					super.insertString(offset, str, attributes);
				}
				else {
					throw new BadLocationException(str, offset);
				}
			}
		});
	}

}
