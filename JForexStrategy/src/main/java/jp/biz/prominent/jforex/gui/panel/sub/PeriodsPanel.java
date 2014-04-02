package jp.biz.prominent.jforex.gui.panel.sub;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import jp.biz.prominent.jforex.entity.StrategyConfig;

import org.apache.commons.lang3.StringUtils;

import com.dukascopy.api.Period;

/**
 * 時間足を表すパネルクラス。
 * 
 * @author famille
 */
public class PeriodsPanel extends JPanel {

	// 時間足
	private ButtonGroup grpPeriods;
	private JRadioButton radio1Minute;
	private JRadioButton radio5Minutes;
	private JRadioButton radio30Minutes;

	/**
	 * 時間足パネルを生成する。
	 */
	public PeriodsPanel() {
		setName("時間足");
		setBorder(new LineBorder(Color.BLACK));

		grpPeriods = new ButtonGroup();

		radio1Minute = new JRadioButton("１分足");
		radio5Minutes = new JRadioButton("５分足");
		radio30Minutes = new JRadioButton("３０分足");

		grpPeriods.add(radio1Minute);
		grpPeriods.add(radio5Minutes);
		grpPeriods.add(radio30Minutes);

		// パネル項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * 時間足パネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(radio1Minute, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(radio5Minutes, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(radio30Minutes, gbc);

		add(radio1Minute);
		add(radio5Minutes);
		add(radio30Minutes);
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		if (radio1Minute.isSelected()) {
			config.setBarPeriods(Period.ONE_MIN.toString());
		}
		else if (radio5Minutes.isSelected()) {
			config.setBarPeriods(Period.FIVE_MINS.toString());
		}
		else if (radio30Minutes.isSelected()) {
			config.setBarPeriods(Period.THIRTY_MINS.toString());
		}
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		String period = config.getBarPeriods();

		if (StringUtils.equals(Period.ONE_MIN.toString(), period)) {
			radio1Minute.setSelected(true);
		}
		else if (StringUtils.equals(Period.FIVE_MINS.toString(), period)) {
			radio5Minutes.setSelected(true);
		}
		else if (StringUtils.equals(Period.THIRTY_MINS.toString(), period)) {
			radio30Minutes.setSelected(true);
		}
	}
}
