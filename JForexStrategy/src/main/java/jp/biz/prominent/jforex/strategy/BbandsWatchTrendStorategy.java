package jp.biz.prominent.jforex.strategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IOrder.State;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

/**
 * ボリンジャーバンドとフラクタルを利用したストラテジー。
 * 
 * @author famille
 */
public class BbandsWatchTrendStorategy implements IStrategy {

	/** コンテキスト情報 */
	private IContext context;
	/** コンソール */
	private IConsole console;
	/** アカウント情報 */
	private IAccount account;
	/** JForexエンジン */
	private IEngine engine;
	/** インジケーター */
	private IIndicators indicators;

	// 設定パラメータ
	/** 通貨ペア（EUR/USD） **/
	@Configurable("通貨ペア（EUR/USD）")
	public boolean isEURUSD = true;

	/** 通貨ペア（GBP/USD） **/
	@Configurable("通貨ペア（GBP/USD）")
	public boolean isGBPUSD = true;

	/** 通貨ペア（EUR/JPY） **/
	@Configurable("通貨ペア（EUR/JPY）")
	public boolean isEURJPY = true;

	/** 通貨ペア（USD/JPY） **/
	@Configurable("通貨ペア（USD/JPY）")
	public boolean isUSDJPY = true;

	/** 通貨ペア */
	private Set<Instrument> instruments;

	/** バーピリオド */
	@Configurable("時間足")
	public Period period = Period.FIVE_MINS;

	/** 残高100$ごとのロット数 */
	@Configurable("初期ロット/残高100$")
	public Double lotPer100 = 1.0;

	/** 最大ロット数 */
	@Configurable("最大ロット")
	public Double lotMax = 10000.0;

	/** エントリーしきい値（PIPS） */
	@Configurable("エントリーしきい値（PIPS）")
	public Double pipsEntryThreshold = 15.0;

	/** 損切りしきい値（PIPS） */
	@Configurable("損切りしきい値（PIPS）")
	public Double pipsLossCut = 10.0;
	
	/** 買い増しモード */
	@Configurable("買い増しモード")
	public boolean nampinFlg = true;
	
	/** 買い（売り）増し上限回数 */
	@Configurable("買い（売り）増し上限回数")
	public int nampinMaxCount = 9;

	/** 買い（売り）増ししきい値（PIPS） */
	@Configurable("買い（売り）増ししきい値（PIPS）")
	public Double pipsAdditionalThreshold = 4.0;

	/** 決済モード１（センターライン） */
	@Configurable("決済モード１（センターライン）")
	public boolean isCloseModeCenterLine = true;
	
	/** 決済モード２（逆フラクタル） */
	@Configurable("決済モード２（逆フラクタル）")
	public boolean isCloseModeRevFractal = false;
	
	/** 決済モード３（１σ内側） */
	@Configurable("決済モード３（１σ内側）")
	public boolean isCloseModeInner1Sigma = false;
	
	/** 再エントリーモード */
	@Configurable("再エントリーモード")
	public boolean isReEntryMode = true;
	
	/** セーフモードＡ */
	@Configurable("セーフモードＡ")
	public boolean isSafeModeA = true;

	/** セーフモードＢ */
	@Configurable("セーフモードＢ")
	public boolean isSafeModeB = true;

	/** デバッグログ出力 */
	@Configurable("デバッグログ出力")
	public boolean isDebug = false;

	/** 許可するスリッページしきい値 */
	private static final double MAX_SLIPPAGE = 5;

	/** オーダー状態更新待機時間 */
	private static final long TIMEOUT_ORDER_UPDATE = 2000;

	/** ラベル用タイムスタンプフォーマット */
	private static final SimpleDateFormat sdfLabel = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/** ログ用タイムスタンプフォーマット */
	private static final SimpleDateFormat sdfLog = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/** 解析状態 */
	private enum AnalyzeState {
		/** なし */
		NONE,
		/** 上バンドタッチ */
		TOUCH_UP,
		/** 下バンドタッチ */
		TOUCH_DOWN,
		/** エントリー不可（中央バンド） */
		NO_ENTRY_MIDDLE_BAND,
		/** エントリー不可（２σ） */
		NO_ENTRY_2SIGMA,
		/** 上バンドタッチからエントリー */
		ENTRY_UP,
		/** 下バンドタッチからエントリー */
		ENTRY_DOWN
	}

	/** 状態マップ */
	private Map<Instrument, AnalyzeState> stateMap = new HashMap<Instrument, AnalyzeState>();

	/** ボリンジャーバンドタッチ値マップ */
	private Map<Instrument, Double> touchPositionMap = new HashMap<Instrument, Double>();
	
	/** 前回終値マップ */
	private Map<Instrument, Double> closeMap = new HashMap<Instrument, Double>();

	/** リトライカウントマップ */
	private Map<Instrument, Integer> retryCountMap = new HashMap<Instrument, Integer>();
	
	/** フラクタル情報マップ */
	private Map<Instrument, FractalAnalyzer> fractalMap = new HashMap<Instrument, FractalAnalyzer>();

	/**
	 * ストラテジーを初期化する。
	 */
	public BbandsWatchTrendStorategy() {
	}

	/**
	 * アカウント情報更新時に呼び出される。
	 * 
	 * @param account アカウント情報
	 */
	@Override
	public void onAccount(IAccount account) throws JFException {
		this.account = account;
	}

	/**
	 * バー完成時に呼び出される。
	 * 
	 * @param instrument 通貨ペア
	 * @param period 時間足
	 * @param askBar 買い値バー
	 * @param bidBar 売り値バー
	 */
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		// 通貨ペアが一致しない場合は終了する
		if (!instruments.contains(instrument)) {
			return;
		}
		// 期間が一致しない場合は終了する
		if (period != this.period) {
			return;
		}

		// 通貨ペアに対応する解析状態を取得する
		if (!stateMap.containsKey(instrument)) {
			stateMap.put(instrument, AnalyzeState.NONE);
		}
		AnalyzeState state = stateMap.get(instrument);

		// 高値と安値と終値を取得する
		double high = bidBar.getHigh();
		double low = bidBar.getLow();
		double close = bidBar.getClose();
		
		// 前回終値を記録する
		closeMap.put(instrument, close);

		// 通貨ペアに対応するボリンジャーバンドを取得する
		BollingerBandsAnalyzer bbands = new BollingerBandsAnalyzer(instrument, getBollingerBands(instrument, period, 2.0), high, low, close);

		// 通貨ペアに対応するフラクタル情報を取得する
		if (!fractalMap.containsKey(instrument)) {
			fractalMap.put(instrument, new FractalAnalyzer(
					context.getHistory(),
					instrument,
					period,
					OfferSide.BID));
		}

		FractalAnalyzer fractal = fractalMap.get(instrument);

		// フラクタル解析情報を追加する
		fractal.add(high, low);

		// フラグがない状態
		if (state == AnalyzeState.NONE) {
			// 上バンドタッチ
			if (bbands.isTouchUpper()) {
				stateMap.put(instrument, AnalyzeState.TOUCH_UP);
				touchPositionMap.put(instrument, bbands.getUpper());
			}
			// 下バンドタッチ
			else if (bbands.isTouchBottom()) {
				stateMap.put(instrument, AnalyzeState.TOUCH_DOWN);
				touchPositionMap.put(instrument, bbands.getBottom());
			}
		}
		// エントリー不可
		else if (state == AnalyzeState.NO_ENTRY_MIDDLE_BAND) {
			// 中間バンドにタッチ
			if (bbands.isTouchMiddle()) {
				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NONE);
				printDebugLog(instrument, state, bidBar.getTime(), "entry enabled.");
			}
		}
		// エントリー不可（２σ）
		else if (state == AnalyzeState.NO_ENTRY_2SIGMA) {
			// 上下バンドにタッチ
			if (bbands.isTouchUpper() || bbands.isTouchBottom()) {
				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NONE);
				printDebugLog(instrument, state, bidBar.getTime(), "entry enabled.");
			}
		}
		// 上・下バンドタッチ状態
		else if (state == AnalyzeState.TOUCH_UP || state == AnalyzeState.TOUCH_DOWN) {
			// セーフモードAで逆方向のフラクタルサインが確定した場合はエントリーしない
			if(isSafeModeA &&
				(state == AnalyzeState.TOUCH_UP && fractal.isDownFractal() ||
				 state == AnalyzeState.TOUCH_DOWN && fractal.isUpFractal())) {
				
				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NO_ENTRY_MIDDLE_BAND);
				printDebugLog(instrument, state, bidBar.getTime(), "entry disabled for safe mode A.");
			}
		}
		// 売りエントリー・買いエントリー
		else if (state == AnalyzeState.ENTRY_UP || state == AnalyzeState.ENTRY_DOWN) {
			// 決済モード２で逆方向のフラクタル
			if(isCloseModeRevFractal) {
				// 逆方向のフラクタルが発生した場合は決済する
				if(state == AnalyzeState.ENTRY_UP && fractal.isDownFractal() ||
					state == AnalyzeState.ENTRY_DOWN && fractal.isUpFractal()) {
					closeAndInit(instrument, state);
					// 再エントリーモードでない場合は、エントリーを無効にする
					if(!isReEntryMode) {
						stateMap.put(instrument, AnalyzeState.NO_ENTRY_MIDDLE_BAND);
					}
				}
			}
		}
	}

	/**
	 * ティック（取引が成立したタイミング）単位で呼び出される。<br>
	 * 価格がそこに差し掛かった瞬間になにかしたい場合などに使用できる。
	 */
	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {
		// 通貨ペアが一致しない場合は終了する
		if (!instruments.contains(instrument)) {
			return;
		}
		// 解析状態が存在しない場合は終了する
		if (!stateMap.containsKey(instrument)) {
			return;
		}
		
		// 通貨ペアに対応する解析状態を取得する
		AnalyzeState state = stateMap.get(instrument);

		// 対象の状態でなければ、処理を終了する
		if (state == AnalyzeState.NONE ||
			state == AnalyzeState.NO_ENTRY_2SIGMA ||
			state == AnalyzeState.NO_ENTRY_MIDDLE_BAND) {
			return;
		}

		// 現在の値を取得する
		double current = tick.getBid();
		// 前回の値を取得する
		double previous = closeMap.get(instrument);
		// 残高から初期ロットを取得
		double initLot = getInitialLot();
		
		// 上・下バンドタッチ状態
		if (state == AnalyzeState.TOUCH_UP || state == AnalyzeState.TOUCH_DOWN) {
			// 2σのボリンジャーバンドを取得する
			double[] bbands = getBollingerBands(instrument, period, 2.0);
			// １σのボリンジャーバンドを取得する
			double[] bbandsInner = getBollingerBands(instrument, period, 1.0);
			
			// セーフモードBで１σ内に入った場合は２σにタッチするまでエントリー不可
			if(isSafeModeB && bbandsInner[0] >= current && current >= bbandsInner[2]) {
				printDebugLog(instrument, state, tick.getTime(), "disable entry for safe mode B.");
				stateMap.put(instrument, AnalyzeState.NO_ENTRY_2SIGMA);
			}
			// 中央バンドにタッチした場合は初期状態にする
			else if((previous < bbands[1] && bbands[1] <= current) ||
					 (previous > bbands[1] && bbands[1] >= current)){
				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NO_ENTRY_MIDDLE_BAND);
				printDebugLog(instrument, state, tick.getTime(), "cancel state for touch middle band.");
			}
			else {
				// ボリンジャーバンドタッチ時の値を取得する
				double touch = touchPositionMap.get(instrument);
				
				// ボリンジャーバンドタッチからの距離
				double distance = (state == AnalyzeState.TOUCH_UP) ? current - touch : touch - current;
				
				// しきい値を超えている場合はエントリーする
				if(distance >= pipsEntryThreshold * instrument.getPipValue()) {
					// オーダーコマンド
					IEngine.OrderCommand command = (state == AnalyzeState.TOUCH_UP) ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL;
	
					// エントリーに成功した場合は、状態を更新する
					if (entry(instrument, state, tick.getTime(), command, initLot, current, 0) != null) {
						stateMap.put(instrument, (state == AnalyzeState.TOUCH_UP) ? AnalyzeState.ENTRY_UP : AnalyzeState.ENTRY_DOWN);
					}
				}
			}
		}
		// 売りエントリー・買いエントリー
		else if (state == AnalyzeState.ENTRY_UP || state == AnalyzeState.ENTRY_DOWN) {
			// ボリンジャーバンドを
			double[] bbands = getBollingerBands(instrument, period, 2.0);
			
			// 通貨ペアに対応するリトライ回数を取得する
			if (!retryCountMap.containsKey(instrument)) {
				retryCountMap.put(instrument, 0);
			}
			int retryCount = retryCountMap.get(instrument);
			
			// 決済モード１で中央バンドタッチ
			if(isCloseModeCenterLine && 
				(previous < bbands[1] && bbands[1] <= current) ||
				(previous > bbands[1] && bbands[1] >= current)){
				closeAndInit(instrument, state);
			}
			// 買い増しモードがオンの場合
			else if(nampinFlg && retryCount < nampinMaxCount) {
				retryCountMap.put(instrument, ++retryCount);
				
				printDebugLog(instrument, state, tick.getTime(), "retry:" + retryCount + "/" + nampinMaxCount);
				
				// オーダーコマンド
				IEngine.OrderCommand command = (state == AnalyzeState.ENTRY_UP) ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL;
				// 買いまし／売りましする
				entry(instrument, state, tick.getTime(), command, initLot, current, retryCount);
			}
		}
	}

	/**
	 * エントリーを実施する。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 状態
	 * @param time 時間
	 * @param command コマンド
	 * @param amount 量
	 * @param close 終値
	 * @return オーダー
	 * @throws JFException エントリーに失敗した場合
	 */
	private IOrder entry(Instrument instrument, AnalyzeState state, long time, OrderCommand command, double amount, double close, int count) throws JFException {
		
		// 損切り
		double stopLoss = (command == OrderCommand.BUY) ? close - pipsLossCut * instrument.getPipValue() : close + pipsLossCut * instrument.getPipValue(); 
		// 利益確定
		double takeProfit = (command == OrderCommand.BUY) ? close + 200 * instrument.getPipValue() : close - 200 * instrument.getPipValue();

		// エントリーする
		IOrder order = engine.submitOrder(
				getLabel(instrument, command, amount, count),
				instrument,
				command,
				amount, // 注文する量
				0, // 価格。０の場合はJForexで確認できた最新価格
				MAX_SLIPPAGE,
				stopLoss,
				takeProfit);

		// オーダーが受理されるまで待機する
		order.waitForUpdate(TIMEOUT_ORDER_UPDATE, State.FILLED, State.CANCELED);

		// 正しく受理されている場合
		if (order.getState() == State.FILLED) {
			printLog(instrument, state, time, "order filled. open price=" + order.getOpenPrice() + ", lots=" + order.getAmount() * 1000);
			return order;
		}
		// キャンセルされた場合
		else {
//			printLog(instrument, state, time, "order canceled.");
			return null;
		}
	}

	/**
	 * オーダー用ラベルを生成する。
	 * 
	 * @param instrument 通貨ペア
	 * @param command オーダーコマンド
	 * @param currency 100万通貨単位
	 * @return オーダー用ラベル
	 */
	private String getLabel(Instrument instrument, OrderCommand command, double currency, int count) {
//		return instrument.name() + "_" + command.name() + "_" + (int) (currency * 1000) + "Lots_" + sdfLabel.format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
		return instrument.name() + "_" + command.name() + "_" + count + "_" + (int) (currency * 1000) + "Lots_" + sdfLabel.format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
	}

	/**
	 * デバッグログを出力する。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 状態
	 * @param time 時間
	 * @param messages メッセージ
	 */
	private void printDebugLog(Instrument instrument, AnalyzeState state, long time, String... messages) {
		// デバッグ出力しない場合は終了する
		if (!isDebug) {
			return;
		}

		// ログ出力する
		printLog(instrument, state, time, messages);
	}

	/**
	 * ログを出力する。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 状態
	 * @param time 時間
	 * @param messages メッセージ
	 */
	private void printLog(Instrument instrument, AnalyzeState state, long time, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages) {
			sb.append(message).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);

		console.getOut().println(formatTime(time) + " | " + instrument + "." + state + ">" + sb.toString());
	}

	/**
	 * 時刻をログ向けの文字列形式で取得する。
	 * 
	 * @param time 時刻
	 * @return ログ向け文字列形式
	 */
	private String formatTime(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(time);
		return sdfLog.format(cal.getTime());
	}

	/**
	 * 新しいメッセージの受信時に呼び出される。
	 * 
	 * @param message 受信メッセージ
	 */
	@Override
	public void onMessage(IMessage message) throws JFException {
	}

	/**
	 * ストラテジー開始時に呼び出される。
	 * 
	 * @param context コンテキスト情報
	 */
	@Override
	public void onStart(IContext context) throws JFException {
		// 開始時の各種コンテキスト情報を保存する
		this.context = context;
		this.console = context.getConsole();
		this.engine = context.getEngine();
		this.indicators = context.getIndicators();

		if (instruments == null) {
			instruments = new HashSet<Instrument>();
			if (isEURUSD) {
				instruments.add(Instrument.EURUSD);
			}
			if (isGBPUSD) {
				instruments.add(Instrument.GBPUSD);
			}
			if (isEURJPY) {
				instruments.add(Instrument.EURJPY);
			}
			if (isUSDJPY) {
				instruments.add(Instrument.USDJPY);
			}
		}
		
		if(!isCloseModeCenterLine && !isCloseModeInner1Sigma && !isCloseModeRevFractal) {
			isCloseModeCenterLine = true;
			console.getInfo().println("[Warning]決済モードがすべてオフだったため、決済モード１をオンにしました。");
		}

		console.getInfo().println("--- Strategy Start ---");
	}

	/**
	 * ストラテジー終了時に呼び出される。
	 */
	@Override
	public void onStop() throws JFException {
		// すべてのオーダーをクローズする
		for (Instrument instrument : this.instruments) {
			closeAndInit(instrument, AnalyzeState.NONE);
		}
		console.getInfo().println("--- Strategy Stop ---");
	}

	/**
	 * ボリンジャーバンドを取得する。
	 * 
	 * @param instrument 通貨ペア
	 * @param period 時間足
	 * @param nbDevUpDown 上下バンドのσ値
	 * @return ボリンジャーバンド。<br>
	 * [0]：上部バンド<br>
	 * [1]：中間バンド<br>
	 * [2]：下部バンド
	 * @throws JFException ボリンジャーバンドの取得に失敗した場合
	 */
	private double[] getBollingerBands(Instrument instrument, Period period, double nbDevUpDown) throws JFException {
		// ボリンジャーバンドを取得して返却する
		return indicators.bbands(
				instrument,
				period,
				OfferSide.BID,
				AppliedPrice.CLOSE,
				20,
				nbDevUpDown, // 上部バンドのσ
				nbDevUpDown, // 下部バンドのσ
				MaType.EMA, // 中間バンドのとり方（指数平均）
				0);// 0：現在のバー、1：１個前のバー、２：２個前のバー・・・
	}

	/**
	 * オーダーのクローズ及び各種情報の初期化を行う。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 解析状態
	 * @throws JFException クローズにて例外が発生した倍
	 */
	private void closeAndInit(Instrument instrument, AnalyzeState state) throws JFException {
		closeAndInit(instrument, state, false);
	}

	/**
	 * オーダーのクローズ及び各種情報の初期化を行う。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 解析状態
	 * @param isLossCut 損切りか否か
	 * @throws JFException クローズにて例外が発生した倍
	 */
	private void closeAndInit(Instrument instrument, AnalyzeState state, boolean isLossCut) throws JFException {
		List<IOrder> orderList = engine.getOrders(instrument);

		for (IOrder order : orderList) {
			// FILLEDのオーダーであれば
			if (order.getState() == State.FILLED) {
				// クローズする
				order.close();
				printClosedOrder(instrument, state, order, isLossCut);
			}
		}

		// 状態を初期化する
		stateMap.remove(instrument);
		// ボリンジャーバンドタッチ値マップを初期化する
		touchPositionMap.remove(instrument);
		// 前回クローズ値マップを初期化する
		closeMap.remove(instrument);
		// リトライカウントを初期化する
		retryCountMap.remove(instrument);
	}

	/**
	 * オーダーのクローズ情報をログ出力する。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 状態
	 * @param order オーダー
	 * @param isLossCut 損切りか否か
	 * @throws JFException ログ出力に失敗した場合
	 */
	private void printClosedOrder(Instrument instrument, AnalyzeState state, IOrder order, boolean isLossCut) throws JFException {
		order.waitForUpdate(State.CLOSED);

		if (!isLossCut) {
			printDebugLog(instrument, state, order.getCloseTime(), "close order." + instrument + ">open=" + order.getOpenPrice() + "(" + formatTime(order.getFillTime()) + "), close=" + order.getClosePrice() + "(" + formatTime(order.getCloseTime()) + "), profit/loss=" + order.getProfitLossInPips() + "PIPS, lots=" + order.getAmount() * 1000);
		}
		else {
			printLog(instrument, state, order.getCloseTime(), "Loss cut : close order." + instrument + ">open=" + order.getOpenPrice() + "(" + formatTime(order.getFillTime()) + "), close=" + order.getClosePrice() + "(" + formatTime(order.getCloseTime()) + "), profit/loss=" + order.getProfitLossInPips() + "PIPS, lots=" + order.getAmount() * 1000);
		}
	}

	/**
	 * 初期ロットを取得する。
	 * 
	 * @return 初期ロット
	 */
	private double getInitialLot() {
		// 残高単位の初期ロットと最大値のうち小さい方の値を初期ロットとする
		return Math.min(
				this.lotPer100 * (int) (account.getBalance() / 100),
				this.lotMax) / 1000;
	}
	
	/**
	 * 配列の文字列表現を取得する。
	 * 
	 * @param array 配列
	 * @return 文字列表現
	 */
	private String join(Iterable<?> array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object value : array) {
			sb.append(value).append(",");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * ボリンジャーバンド解析用クラス。
	 * 
	 * @author famille
	 */
	private class BollingerBandsAnalyzer {
		/** 通貨ペア */
		private Instrument instrument;
		/** 上部バンド */
		private double upper;
		/** 中間バンド */
		private double middle;
		/** 下部バンド */
		private double bottom;
		/** 高値 */
		private double high;
		/** 安値 */
		private double low;
		/** 終値 */
		private double close;

		/**
		 * ボリンジャーバンド解析用インスタンスを生成する。
		 * 
		 * @param instrument 通貨ペア
		 * @param bbands ボリンジャーバンド
		 * @param high 高値
		 * @param low 安値
		 * @param close 終値
		 */
		public BollingerBandsAnalyzer(Instrument instrument, double[] bbands, double high, double low, double close) {
			this.instrument = instrument;
			this.upper = bbands[0];
			this.middle = bbands[1];
			this.bottom = bbands[2];
			this.high = high;
			this.low = low;
			this.close = close;
		}

		/**
		 * 上部バンドを取得する。
		 * 
		 * @return 上部バンド
		 */
		public double getUpper() {
			return upper;
		}

		/**
		 * 中間バンドを取得する。
		 * 
		 * @return 中間バンド
		 */
		public double getMiddle() {
			return middle;
		}

		/**
		 * 下部バンドを取得する。
		 * 
		 * @return 下部バンド
		 */
		public double getBottom() {
			return bottom;
		}

		/**
		 * バーが上部バンドにタッチしているか否かを判定する。
		 * 
		 * @return 判定結果。<br>true：タッチしている、false：タッチしていない
		 */
		public boolean isTouchUpper() {
			return isTouch(upper);
		}

		/**
		 * バーが中間バンドにタッチしているか否かを判定する。
		 * 
		 * @return 判定結果。<br>true：タッチしている、false：タッチしていない
		 */
		public boolean isTouchMiddle() {
			return isTouch(middle);
		}

		/**
		 * バーが下部バンドにタッチしているか否かを判定する。
		 * 
		 * @return 判定結果。<br>true：タッチしている、false：タッチしていない
		 */
		public boolean isTouchBottom() {
			return isTouch(bottom);
		}

		/**
		 * 終値と中間バンドの距離を取得する。
		 * 
		 * @return 終値と中間バンドの距離
		 */
		public double getDistanceFromMiddle() {
			// PIPS単位に修正する
			return (Math.abs(close - middle) / instrument.getPipValue());
		}

		/**
		 * バーが基準値にタッチしているか否かを判定する。
		 * 
		 * @param target 基準値
		 * @return 判定結果。<br>true：タッチしている、false：タッチしていない
		 */
		private boolean isTouch(double target) {
			return high > target && target > low;
		}

		/**
		 * ボリンジャーバンド解析結果の文字列表現を取得する。
		 * 
		 * @return ボリンジャーバンド解析結果の文字列表現
		 */
		@Override
		public String toString() {
			return "{" +
					"bar:{low:" + low + ",high:" + high + ",close:" + close + "}," +
					"band:{upper:" + getUpper() + ",middle:" + getMiddle() + ",bottom:" + getBottom() + "}," +
					"isTouch:{upper:" + isTouchUpper() + ",middle:" + isTouchMiddle() + ",bottom:" + isTouchBottom() + "}," +
					"distanceFromMiddle:" + getDistanceFromMiddle() +
					"}";
		}

		/**
		 * ボリンジャーバンド解析結果のシンプル文字列表現を取得する。
		 * 
		 * @return ボリンジャーバンド解析結果のシンプル文字列表現
		 */
		@SuppressWarnings("unused")
		public String toSimpleString() {
			return (isTouchUpper() ? " touch upper" : "") + (isTouchBottom() ? " touch down" : "") + (isTouchMiddle() ? " touch middle" : " distanceFromMiddle:" + getDistanceFromMiddle());
		}
	}

	/**
	 * フラクタル解析用クラス。
	 * 
	 * @author famille
	 */
	private class FractalAnalyzer {

		/** フラクタル解析に必要な履歴の数 */
		private static final int HISTORY_SIZE = 5;

		/** フラクタルの基準となる位置 */
		private static final int BASE_INDEX = 2;

		/** 高値の履歴 */
		private List<Double> highHistory = new ArrayList<Double>();

		/** 安値の履歴 */
		private List<Double> lowHistory = new ArrayList<Double>();

		/**
		 * フラクタル履歴を初期化する。
		 * 
		 * @param history 履歴情報
		 * @param instrument 通貨ペア
		 * @param period 時間足
		 * @param side オファーサイド
		 */
		public FractalAnalyzer(IHistory history, Instrument instrument, Period period, OfferSide side) {
			try {
				for (int i = HISTORY_SIZE; i > 0; i--) {
					IBar bar = history.getBar(instrument, period, side, i);
					highHistory.add(bar.getHigh());
					lowHistory.add(bar.getLow());
				}
			}
			catch (JFException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 履歴を追加する。
		 * 
		 * @param high 高値
		 * @param low 安値
		 */
		public void add(double high, double low) {
			highHistory.add(high);
			lowHistory.add(low);

			if (highHistory.size() > HISTORY_SIZE) {
				highHistory.remove(0);
			}
			if (lowHistory.size() > HISTORY_SIZE) {
				lowHistory.remove(0);
			}
		}

		/**
		 * 上向きフラクタルが確定したか否かを判定する。<br>
		 * ※確定＝売りサインの基準となる。
		 * 
		 * @return 判定結果。<br>true：フラクタル確定、false：フラクタル無し
		 */
		public boolean isUpFractal() {
			// 履歴が解析基準のサイズより小さい場合はフラクタル無しとする
			if (highHistory.size() < HISTORY_SIZE) {
				return false;
			}

			// 基準の高値が前後２本の高値より高ければ、フラクタル有りとする
			return highHistory.get(BASE_INDEX) == Collections.max(highHistory);
		}

		/**
		 * 下向きフラクタルが確定したか否かを判定する。<br>
		 * ※確定＝買いサインの基準となる。
		 * 
		 * @return 判定結果。<br>true：フラクタル確定、false：フラクタル無し
		 */
		public boolean isDownFractal() {
			// 履歴が解析基準のサイズより小さい場合はフラクタル無しとする
			if (lowHistory.size() < HISTORY_SIZE) {
				return false;
			}

			// 基準の安値が前後２本の安値より低ければ、フラクタル有りとする
			return lowHistory.get(BASE_INDEX) == Collections.min(lowHistory);
		}

		/**
		 * フラクタル解析情報を表す文字列を返却する。
		 * 
		 * @return フラクタル解析情報文字列
		 */
		@Override
		public String toString() {
			return "{mark:" + (isUpFractal() ? "⇓" : "") + (isDownFractal() ? "⇑" : "") + ",high:" + join(highHistory) + ",low:" + join(lowHistory) + "}";
		}

		/**
		 * フラクタル解析情報を表すシンプルな文字列を返却する。
		 * 
		 * @return フラクタル解析情報シンプル文字列
		 */
		@SuppressWarnings("unused")
		public String toSimpleString() {
			return (isUpFractal() ? "⇓" : "") + (isDownFractal() ? "⇑" : "");
		}
	}
}