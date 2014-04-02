package jp.biz.prominent.jforex.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jp.biz.prominent.jforex.db.StrategyConfigAccess;
import jp.biz.prominent.jforex.entity.StrategyConfig;
import jp.biz.prominent.jforex.gui.panel.sub.AdditionalPanel;
import jp.biz.prominent.jforex.gui.panel.sub.ButtonPanel;
import jp.biz.prominent.jforex.gui.panel.sub.EntryPanel;
import jp.biz.prominent.jforex.gui.panel.sub.InstrumentsPanel;
import jp.biz.prominent.jforex.gui.panel.sub.LossCutPanel;
import jp.biz.prominent.jforex.gui.panel.sub.LotsPanel;
import jp.biz.prominent.jforex.gui.panel.sub.PeriodsPanel;
import jp.biz.prominent.jforex.strategy.BbandsAndFractalStorategy;
import jp.co.famille.util.di.InjectionUtils;
import jp.co.famille.util.swing.ShowDialogUtil;

import com.dukascopy.api.system.IClient;

/**
 * ストラテジー設定及び開始・停止を実行する画面用パネルクラス。
 * 
 * @author famille
 */
public class StrategyConfigPanel extends JPanel {

	// アクセス用のインスタンスを生成する
	private StrategyConfigAccess access = InjectionUtils.getInstance(StrategyConfigAccess.class);

	// 通貨ペア
	private InstrumentsPanel pnlInstruments;

	// 時間足
	private PeriodsPanel pnlPeriods;

	// ロット数
	private LotsPanel pnlLots;

	// エントリー条件
	private EntryPanel pnlEntry;

	// 逆行時
	private AdditionalPanel pnlAddition;

	// 損切り
	private LossCutPanel pnlLossCut;

	// ボタン用
	private ButtonPanel pnlButton;

	// ストラテジー
	private BbandsAndFractalStorategy strategy;

	// プロセスID
	private Long processId;

	/**
	 * ストラテジー設定画面を生成する。
	 * 
	 * @param client JForexクライアント
	 */
	public StrategyConfigPanel(final IClient client) {

		// 通貨ペア
		pnlInstruments = new InstrumentsPanel();

		// 時間足
		pnlPeriods = new PeriodsPanel();

		// ロット数
		pnlLots = new LotsPanel();

		// エントリー条件
		pnlEntry = new EntryPanel();

		// 逆行時
		pnlAddition = new AdditionalPanel();

		// 損切り
		pnlLossCut = new LossCutPanel();

		// ボタン用
		pnlButton = new ButtonPanel();

		// 開始ボタン押下時の処理を定義
		pnlButton.getBtnStart().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 確認ダイアログにてOKボタンが押下された場合
				if (ShowDialogUtil.confirm("ストラテジーを開始します。よろしいですか。") == JOptionPane.OK_OPTION) {
					// 画面の内容をもとに、ストラテジー設定を生成する
					StrategyConfig config = toEntity();
					// ストラテジーを生成する
					strategy = new BbandsAndFractalStorategy();
					// 取引対象となる通貨ペアを設定する
					client.setSubscribedInstruments(config.getInstrumentSet());
					// ストラテジーを開始する
					processId = client.startStrategy(strategy);
					// チャートを表示する
					displayChart(config);

					pnlButton.getBtnStart().setEnabled(false);
					pnlButton.getBtnStop().setEnabled(true);
					pnlButton.getBtnTerminate().setEnabled(true);

				}
			}

			/**
			 * チャートを表示する。
			 * 
			 * @param config ストラテジー設定
			 */
			private void displayChart(StrategyConfig config) {
				// IIndicators indicators = strategy.getIndicators();
				//
				// JFrame frame = new JFrame("チャート");
				// frame.setLayout(null);
				// frame.setSize(800, 600);
				//
				// int x = 0;
				// int y = 0;
				//
				// for (Instrument instrument : config.getInstrumentSet()) {
				// IFeedDescriptor feedDescriptor = new
				// RangeBarFeedDescriptor(instrument, PriceRange.FIVE_PIPS,
				// OfferSide.BID, config.getPeriod());
				// feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
				//
				// IChart chart = client.openChart(feedDescriptor);
				//
				// chart.add(indicators.getIndicator("BBANDS"));
				// chart.add(indicators.getIndicator("FRACTAL"));
				//
				// final IClientGUI clientGUI = client.getClientGUI(chart);
				// JLabel label = new JLabel(instrument.toString(),
				// SwingConstants.LEFT);
				// label.setBounds(x, y, 390, 20);
				// JPanel panel = clientGUI.getChartPanel();
				// panel.setBounds(x, y + 20, 390, 240);
				//
				// if (x == 0) {
				// x += 400;
				// }
				// else {
				// x = 0;
				// y += 300;
				// }
				//
				// frame.add(label);
				// frame.add(panel);
				// }
				//
				// frame.setVisible(true);

			}
		});

		// 停止ボタン押下時の処理を定義
		pnlButton.getBtnStop().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 確認ダイアログにてOKボタンが押下された場合
				if (ShowDialogUtil.confirm("ストラテジーを停止します。よろしいですか。") == JOptionPane.OK_OPTION) {
					client.stopStrategy(processId);
					processId = null;

					pnlButton.getBtnStart().setEnabled(true);
					pnlButton.getBtnStop().setEnabled(false);
					pnlButton.getBtnTerminate().setEnabled(false);

					exit();
				}
			}
		});

		// 強制停止ボタン押下時の処理を定義
		pnlButton.getBtnTerminate().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 確認ダイアログにてOKボタンが押下された場合
				if (ShowDialogUtil.confirm("ストラテジーを強制停止します。よろしいですか。") == JOptionPane.OK_OPTION) {
					client.stopStrategy(processId);
					processId = null;

					pnlButton.getBtnStart().setEnabled(true);
					pnlButton.getBtnStop().setEnabled(false);
					pnlButton.getBtnTerminate().setEnabled(false);

					exit();
				}
			}
		});

		// テーブルがなければ作成する
		access.createTable();

		// 前回ストラテジー設定を取得する
		StrategyConfig login = access.select();

		// 前回ストラテジー設定を画面に展開する
		toDisplayItems(login);

		// 画面項目のレイアウトを設定する
		setItemLayout();
	}

	/**
	 * アプリケーションの終了時処理を実行する。
	 */
	public void exit() {
		// 保存していたストラテジー設定を削除する
		access.delete();

		// 画面項目からストラテジー設定を取得する
		StrategyConfig login = toEntity();

		// 現在のストラテジー設定を保存する
		access.insert(login);
	}

	/**
	 * ストラテジー設定画面のレイアウトを設定する。
	 */
	private void setItemLayout() {
		setLayout(null);

		pnlInstruments.setBounds(10, 10, 100, 180);
		pnlPeriods.setBounds(120, 10, 100, 180);
		pnlLots.setBounds(230, 10, 200, 100);
		pnlEntry.setBounds(230, 120, 200, 70);
		pnlAddition.setBounds(440, 10, 300, 100);
		pnlLossCut.setBounds(440, 120, 300, 70);
		pnlButton.setBounds(10, 190, 800, 50);

		add(pnlInstruments);
		add(pnlPeriods);
		add(pnlLots);
		add(pnlEntry);
		add(pnlAddition);
		add(pnlLossCut);
		add(pnlButton);
	}

	/**
	 * 画面項目からストラテジー設定を生成する。
	 * 
	 * @return ストラテジー設定
	 */
	private StrategyConfig toEntity() {
		StrategyConfig config = new StrategyConfig();

		// 通貨ペア
		pnlInstruments.loadEntity(config);

		// 時間足
		pnlPeriods.loadEntity(config);

		// ロット数
		pnlLots.loadEntity(config);

		// エントリー条件
		pnlEntry.loadEntity(config);

		// 逆行時
		pnlAddition.loadEntity(config);

		// 損切り
		pnlLossCut.loadEntity(config);

		return config;
	}

	/**
	 * ストラテジー設定を画面項目に展開する。
	 * 
	 * @param config ストラテジー設定
	 */
	private void toDisplayItems(StrategyConfig config) {
		if (config == null) {
			config = new StrategyConfig();
		}

		pnlInstruments.loadDisplayItems(config);
		pnlPeriods.loadDisplayItems(config);
		pnlLots.loadDisplayItems(config);
		pnlEntry.loadDisplayItems(config);
		pnlAddition.loadDisplayItems(config);
		pnlLossCut.loadDisplayItems(config);
	}
}
