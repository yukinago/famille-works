package jp.biz.prominent.jforex.gui.panel.sub;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import jp.biz.prominent.jforex.entity.StrategyConfig;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.dukascopy.api.Instrument;

/**
 * 通貨ペアを表すパネルクラス。
 * 
 * @author famille
 */
public class InstrumentsPanel extends JPanel {

	// 通貨ペア
	private JCheckBox chkEURUSD;
	private JCheckBox chkGBPUSD;
	private JCheckBox chkEURJPY;
	private JCheckBox chkUSDJPY;

	/**
	 * 通貨ペアパネルを生成する。
	 */
	public InstrumentsPanel() {
		setName("通貨ペア");
		setBorder(new LineBorder(Color.BLACK));

		chkEURUSD = new JCheckBox(Instrument.EURUSD.toString());
		chkGBPUSD = new JCheckBox(Instrument.GBPUSD.toString());
		chkEURJPY = new JCheckBox(Instrument.EURJPY.toString());
		chkUSDJPY = new JCheckBox(Instrument.USDJPY.toString());

		// 画面項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * 画面項目の値をエンティティにロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadEntity(StrategyConfig config) {
		List<String> instrumentList = new ArrayList<String>();
		if (chkEURUSD.isSelected()) {
			instrumentList.add(Instrument.EURUSD.toString());
		}
		if (chkGBPUSD.isSelected()) {
			instrumentList.add(Instrument.GBPUSD.toString());
		}
		if (chkEURJPY.isSelected()) {
			instrumentList.add(Instrument.EURJPY.toString());
		}
		if (chkUSDJPY.isSelected()) {
			instrumentList.add(Instrument.USDJPY.toString());
		}

		if (!instrumentList.isEmpty()) {
			config.setInstruments(StringUtils.join(instrumentList, ","));
		}
	}

	/**
	 * 通貨ペアパネルのレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(chkEURUSD, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(chkGBPUSD, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(chkEURJPY, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		layout.setConstraints(chkUSDJPY, gbc);

		add(chkEURUSD);
		add(chkGBPUSD);
		add(chkEURJPY);
		add(chkUSDJPY);
	}

	/**
	 * エンティティから画面項目に値をロードする。
	 * 
	 * @param config エンティティ
	 */
	public void loadDisplayItems(StrategyConfig config) {
		String[] instruments = StringUtils.split(config.getInstruments(), ",");

		chkEURUSD.setSelected(ArrayUtils.contains(instruments, Instrument.EURUSD.toString()));
		chkGBPUSD.setSelected(ArrayUtils.contains(instruments, Instrument.GBPUSD.toString()));
		chkEURJPY.setSelected(ArrayUtils.contains(instruments, Instrument.EURJPY.toString()));
		chkUSDJPY.setSelected(ArrayUtils.contains(instruments, Instrument.USDJPY.toString()));
	}
}
