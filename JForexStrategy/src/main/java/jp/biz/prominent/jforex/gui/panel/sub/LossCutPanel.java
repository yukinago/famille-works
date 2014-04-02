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
 * 損切り設定を表すパネルクラス。
 * 
 * @author famille
 */
public class LossCutPanel extends JPanel {

	// 損切り設定
	private JLabel lblRetryCount;
	private JTextField txtRetryCount;
	private JLabel lblRetryCountSuffix;
	private JLabel lblLossCut;
	private JTextField txtLossCut;
	private JLabel lblLossCutSuffix;

	/**
	 * 損切り設定パネルを生成する。
	 */
	public LossCutPanel() {
		setName("損切り設定");
		setBorder(new LineBorder(Color.BLACK));

		lblRetryCount = new JLabel("最大リトライ");
		txtRetryCount = new JTextField(10);
		txtRetryCount.setHorizontalAlignment(JTextField.RIGHT);
		lblRetryCountSuffix = new JLabel("回");

		lblLossCut = new JLabel("損切り");
		txtLossCut = new JTextField(10);
		txtLossCut.setHorizontalAlignment(JTextField.RIGHT);
		lblLossCutSuffix = new JLabel("PIPS以上");

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * 損切り設定パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblRetryCount, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtRetryCount, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblRetryCountSuffix, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblLossCut, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtLossCut, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblLossCutSuffix, gbc);

		add(lblRetryCount);
		add(txtRetryCount);
		add(lblRetryCountSuffix);
		add(lblLossCut);
		add(txtLossCut);
		add(lblLossCutSuffix);
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		config.setRetryCountAdditional(Integer.valueOf(txtRetryCount.getText()));
		config.setPipsLossCut(Integer.valueOf(txtLossCut.getText()));
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		txtRetryCount.setText(String.valueOf(config.getRetryCountAdditional()));
		txtLossCut.setText(String.valueOf(config.getPipsLossCut()));
	}
}
