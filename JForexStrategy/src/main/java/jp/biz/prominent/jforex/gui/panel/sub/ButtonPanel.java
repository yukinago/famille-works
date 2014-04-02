package jp.biz.prominent.jforex.gui.panel.sub;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import lombok.Data;

/**
 * ボタン領域を表すパネルクラス。
 * 
 * @author famille
 */
@Data
public class ButtonPanel extends JPanel {

	// ボタン領域
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnTerminate;

	/**
	 * ボタン領域パネルを生成する。
	 */
	public ButtonPanel() {
		btnStart = new JButton("開始");
		btnStop = new JButton("終了");
		btnTerminate = new JButton("強制終了");

		btnStop.setEnabled(false);
		btnTerminate.setEnabled(false);

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * ボタン領域パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(btnStart, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(btnStop, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(btnTerminate, gbc);

		add(btnStart);
		add(btnStop);
		add(btnTerminate);
	}
}
