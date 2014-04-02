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
 * ロット数を表すパネルクラス。
 * 
 * @author famille
 */
public class LotsPanel extends JPanel {

	// ロット数
	private JLabel lblInitialLots;
	private JTextField txtInitialLots;
	private JLabel lblMaxLots;
	private JTextField txtMaxLots;

	/**
	 * ロット数パネルを生成する。
	 */
	public LotsPanel() {
		setName("ロット数");
		setBorder(new LineBorder(Color.BLACK));

		lblInitialLots = new JLabel("初期");
		txtInitialLots = new JTextField(10);
		txtInitialLots.setHorizontalAlignment(JTextField.RIGHT);
		lblMaxLots = new JLabel("最大");
		txtMaxLots = new JTextField(10);
		txtMaxLots.setHorizontalAlignment(JTextField.RIGHT);

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * ロット数パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblInitialLots, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtInitialLots, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(lblMaxLots, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(txtMaxLots, gbc);

		add(lblInitialLots);
		add(txtInitialLots);
		add(lblMaxLots);
		add(txtMaxLots);
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		config.setLotPer100(Double.valueOf(txtInitialLots.getText()));
		config.setLotMax(Double.valueOf(txtMaxLots.getText()));
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		txtInitialLots.setText(String.valueOf(config.getLotPer100()));
		txtMaxLots.setText(String.valueOf(config.getLotMax()));
	}
}
