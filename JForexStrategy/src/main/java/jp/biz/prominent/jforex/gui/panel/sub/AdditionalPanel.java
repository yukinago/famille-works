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
 * 逆行時設定を表すパネルクラス。
 * 
 * @author famille
 */
public class AdditionalPanel extends JPanel {

	// 逆行時設定
	private JLabel lblAdditionalLots;
	private JTextField txtAdditionalLots;
	private JLabel lblAdditionalLotsSuffix;
	private JLabel lblAdditionalThreshold;
	private JTextField txtAdditionalThreshold;
	private JLabel lblAdditionalThresholdSuffix;

	/**
	 * 逆行時設定パネルを生成する。
	 */
	public AdditionalPanel() {
		setName("逆行時設定");
		setBorder(new LineBorder(Color.BLACK));

		lblAdditionalLots = new JLabel("買い増し/売り増し");
		txtAdditionalLots = new JTextField(10);
		txtAdditionalLots.setHorizontalAlignment(JTextField.RIGHT);
		lblAdditionalLotsSuffix = new JLabel("ロット");

		lblAdditionalThreshold = new JLabel("直前エントリーから");
		txtAdditionalThreshold = new JTextField(10);
		txtAdditionalThreshold.setHorizontalAlignment(JTextField.RIGHT);
		lblAdditionalThresholdSuffix = new JLabel("PIPS以上");

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * 逆行時設定パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		layout.setConstraints(lblAdditionalLots, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtAdditionalLots, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblAdditionalLotsSuffix, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		layout.setConstraints(lblAdditionalThreshold, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtAdditionalThreshold, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblAdditionalThresholdSuffix, gbc);

		add(lblAdditionalLots);
		add(txtAdditionalLots);
		add(lblAdditionalLotsSuffix);
		add(lblAdditionalThreshold);
		add(txtAdditionalThreshold);
		add(lblAdditionalThresholdSuffix);
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		config.setLotAdditional(Double.valueOf(txtAdditionalLots.getText()));
		config.setPipsAdditionalThreshold(Integer.valueOf(txtAdditionalThreshold.getText()));
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		txtAdditionalLots.setText(String.valueOf(config.getLotAdditional()));
		txtAdditionalThreshold.setText(String.valueOf(config.getPipsAdditionalThreshold()));
	}
}
