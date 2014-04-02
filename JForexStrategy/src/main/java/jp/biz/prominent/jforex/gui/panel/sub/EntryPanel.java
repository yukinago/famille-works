package jp.biz.prominent.jforex.gui.panel.sub;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import jp.biz.prominent.jforex.entity.StrategyConfig;

/**
 * エントリー条件を表すパネルクラス。
 * 
 * @author famille
 */
public class EntryPanel extends JPanel {

	// エントリー条件
	private JLabel lblEntryThreshold;
	private JTextField txtEntryThreshold;
	private JLabel lblEntryThresholdSuffix;

	/**
	 * エントリー条件パネルを生成する。
	 */
	public EntryPanel() {
		setName("エントリー条件");
		setBorder(new LineBorder(Color.BLACK));

		lblEntryThreshold = new JLabel("センターラインから");
		txtEntryThreshold = new JTextField(10);
		txtEntryThreshold.setHorizontalAlignment(JTextField.RIGHT);
		lblEntryThresholdSuffix = new JLabel("PIPS以上");

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * エントリー条件パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		layout.setConstraints(lblEntryThreshold, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtEntryThreshold, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblEntryThresholdSuffix, gbc);

		add(lblEntryThreshold);
		add(txtEntryThreshold);
		add(lblEntryThresholdSuffix);
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		config.setPipsEntryThreshold(Integer.valueOf(txtEntryThreshold.getText()));
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		txtEntryThreshold.setText(String.valueOf(config.getPipsEntryThreshold()));
	}
}
