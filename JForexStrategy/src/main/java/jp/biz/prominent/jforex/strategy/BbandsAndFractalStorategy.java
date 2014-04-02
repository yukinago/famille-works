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
public class BbandsAndFractalStorategy implements IStrategy {

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
	public Double lotPer100 = 0.5;

	/** 最大ロット数 */
	@Configurable("最大ロット")
	public Double lotMax = 10000.0;

	/** エントリーしきい値：下限（PIPS） */
	@Configurable("エントリーしきい値：下限（PIPS）")
	public Double pipsEntryThresholdFrom = 4.0;

	/** エントリーしきい値：上限（PIPS） */
	@Configurable("エントリーしきい値：上限（PIPS）")
	public Double pipsEntryThresholdTo = 25.0;

	/** 買い（売り）増し増分ロット数 */
	@Configurable("買い（売り）増し増分ロット数")
	public Double lotAdditional = 2.0;

	/** 買い（売り）増ししきい値（PIPS） */
	@Configurable("買い（売り）増ししきい値（PIPS）")
	public Double pipsAdditionalThreshold = 4.0;

	/** 買い（売り）増しリトライ回数 */
	@Configurable("買い（売り）増しリトライ回数")
	public Integer retryCountAdditional = 6;

	/** 損切りしきい値（最大リトライ後からのPIPS） */
	@Configurable("損切りしきい値（PIPS）")
	public Double pipsLossCut = 15.0;

	/** セーフモードＡ */
	@Configurable("セーフモードＡ")
	public boolean isSafeModeA = true;

	/** セーフモードＢ */
	@Configurable("セーフモードＢ")
	public boolean isSafeModeB = false;

	/** セーフモードＣ */
	@Configurable("セーフモードＣ")
	public boolean isSafeModeC = true;

	/** 決済待ち時間足の最大本数 */
	@Configurable("セーフモードＢ最大本数")
	public Integer maxPeriod = 12;

	/** 週・月末端回避 */
	@Configurable("週初／週末、月初／月末回避")
	public boolean isAvoidStartEnd = false;

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
		/** エントリー不可 */
		NO_ENTRY,
		/** 上バンドタッチからエントリー */
		ENTRY_UP,
		/** 下バンドタッチからエントリー */
		ENTRY_DOWN
	}

	/** フラクタル情報マップ */
	private Map<Instrument, FractalAnalyzer> fractalMap = new HashMap<Instrument, FractalAnalyzer>();

	/** 状態マップ */
	private Map<Instrument, AnalyzeState> stateMap = new HashMap<Instrument, AnalyzeState>();

	/** リトライカウントマップ */
	private Map<Instrument, Integer> retryCountMap = new HashMap<Instrument, Integer>();

	/** 上下バンドタッチ後カウンタマップ */
	private Map<Instrument, Integer> offsetMap = new HashMap<Instrument, Integer>();

	/** 終値記録マップ */
	private Map<Instrument, Double> closeMap = new HashMap<Instrument, Double>();

	/** クローズ期限マップ（セーフモードＢ時のみ使用） */
	private Map<Instrument, Long> closeLimitMap = new HashMap<Instrument, Long>();

	/** オーダーリストマップ */
	private Map<Instrument, List<IOrder>> orderListMap = new HashMap<Instrument, List<IOrder>>();

	/**
	 * ストラテジーを初期化する。
	 */
	public BbandsAndFractalStorategy() {
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

		// 取引対象外であれば終了する
		if (!isActiveTime(bidBar.getTime(), getPositionTimeLimit(period))) {
			// エントリー待ちを解除する
			if (state == AnalyzeState.TOUCH_UP || state == AnalyzeState.TOUCH_DOWN) {
				stateMap.put(instrument, AnalyzeState.NONE);
			}
			return;
		}

		// 高値と安値と終値を取得する
		double high = bidBar.getHigh();
		double low = bidBar.getLow();
		double close = bidBar.getClose();

		// 終値を記録する
		closeMap.put(instrument, close);

		// 通貨ペアに対応するリトライ回数を取得する
		if (!retryCountMap.containsKey(instrument)) {
			retryCountMap.put(instrument, 0);
		}
		int retryCount = retryCountMap.get(instrument);

		// 通貨ペアに対応するボリンジャーバンドを取得する
		BollingerBandsAnalyzer bbands = new BollingerBandsAnalyzer(instrument, getBollingerBands(instrument, period), high, low, close);

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

		// 残高から初期ロットを取得
		double initLot = getInitialLot();

		// フラグがない状態
		if (state == AnalyzeState.NONE) {
			// 上バンドタッチ
			if (bbands.isTouchUpper()) {
				// 共通ログを出力する
				printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

				state = AnalyzeState.TOUCH_UP;
				stateMap.put(instrument, state);

				// タッチしてから何本目のろうそく足かのカウンタを初期化する
				offsetMap.put(instrument, 0);
			}
			// 下バンドタッチ
			else if (bbands.isTouchBottom()) {
				// 共通ログを出力する
				printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

				state = AnalyzeState.TOUCH_DOWN;
				stateMap.put(instrument, state);

				// タッチしてから何本目のろうそく足かを初期化する
				offsetMap.put(instrument, 0);
			}
		}
		// エントリー不可
		else if (state == AnalyzeState.NO_ENTRY) {
			// 中間バンドにタッチ
			if (bbands.isTouchMiddle()) {
				// 共通ログを出力する
				printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NONE);
				printLog(instrument, state, bidBar.getTime(), "entry enabled.");
			}
		}
		// 上・下バンドタッチ状態
		else if (state == AnalyzeState.TOUCH_UP || state == AnalyzeState.TOUCH_DOWN) {
			// ボリンジャーバンドタッチ後の位置を加算する
			offsetMap.put(instrument, offsetMap.get(instrument) + 1);
			// 位置を取得する
			int offset = offsetMap.get(instrument);

			// エントリー判断（フラクタルの確定）
			boolean entryFlg = (state == AnalyzeState.TOUCH_UP) ? fractal.isUpFractal() : fractal.isDownFractal();
			// 中間バンドを超えているか否か
			boolean overMiddleBand = (state == AnalyzeState.TOUCH_UP) ? close <= bbands.getMiddle() : close >= bbands.getMiddle();

			// 中間バンドにタッチ
			if (bbands.isTouchMiddle()) {
				// 共通ログを出力する
				printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

				// 状態を初期化して終了する
				stateMap.put(instrument, AnalyzeState.NONE);
				printDebugLog(instrument, state, bidBar.getTime(), "touch middle band. cancel state.");
			}
			// フラクタルが確定
			else if (offset >= 2 && entryFlg) {
				// 共通ログを出力する
				printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

				// 終値と中間バンドの距離が、しきい値の下限以下
				if (overMiddleBand || bbands.getDistanceFromMiddle() < this.pipsEntryThresholdFrom) {
					// エントリーを見送り
					printDebugLog(instrument, state, bidBar.getTime(), "distance from middle band is too short. entry skipped.");
				}
				// 終値と中間バンドの距離が、しきい値の上限以上
				else if (bbands.getDistanceFromMiddle() > this.pipsEntryThresholdTo) {
					printLog(instrument, state, bidBar.getTime(), "distance from middle band is too long. entry disabled.");
					// エントリーを無効にする
					stateMap.put(instrument, AnalyzeState.NO_ENTRY);
				}
				else {
					// オーダーコマンド
					IEngine.OrderCommand command = (state == AnalyzeState.TOUCH_UP) ? IEngine.OrderCommand.SELL : IEngine.OrderCommand.BUY;
					IOrder order = null;

					// エントリーに成功した場合は、状態を更新する
					if ((order = entry(instrument, state, bidBar.getTime(), command, initLot, close)) != null) {
						stateMap.put(instrument, (state == AnalyzeState.TOUCH_UP) ? AnalyzeState.ENTRY_UP : AnalyzeState.ENTRY_DOWN);

						List<IOrder> orderList = new ArrayList<IOrder>();
						orderList.add(order);
						orderListMap.put(instrument, orderList);
					}
				}
			}
		}
		// 売りエントリー・買いエントリー
		else if (state == AnalyzeState.ENTRY_UP || state == AnalyzeState.ENTRY_DOWN) {
			// クローズ期限を取得する
			Long closeLimit = closeLimitMap.get(instrument);

			synchronized (instrument) {
				// 通貨ペアに対応する前回エントリー値を取得する
				List<IOrder> orderList = orderListMap.get(instrument);
				Double previousEntry = orderList.get(orderList.size() - 1).getOpenPrice();

				// ナンピン判定
				boolean nampinFractal = (state == (AnalyzeState.ENTRY_UP)) ?
						close > previousEntry && fractal.isUpFractal() :
						close < previousEntry && fractal.isDownFractal();

				// 中間バンドにタッチ
				if (bbands.isTouchMiddle()) {
					// 共通ログを出力する
					printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);
					// クローズし、各種情報を初期化する
					closeAndInit(instrument, state);
				}
				// セーフモードＢでクローズ期限切れ
				else if (isSafeModeB && closeLimit <= bidBar.getTime()) {
					// 共通ログを出力する
					printLog(instrument, state, bidBar.getTime(), "safe mode B: close all orders. entry disabled.");
					// クローズし、各種情報を初期化する
					closeAndInit(instrument, state, true);
					// エントリーを無効にする
					stateMap.put(instrument, AnalyzeState.NO_ENTRY);
				}
				// 逆行し、再度フラクタルが確定
				else if (nampinFractal && retryCount < retryCountAdditional) {
					// 共通ログを出力する
					printCommonInfoLog(instrument, state, bidBar.getTime(), bbands, fractal);

					double distance = ((state == (AnalyzeState.ENTRY_UP)) ?
							(close - previousEntry) : (previousEntry - close))
							/ instrument.getPipValue();

					printDebugLog(instrument, state, bidBar.getTime(),
							"previous entry=" + previousEntry + ", current=" + close,
							"Distance from previous entry:" + distance + "PIPS");

					// かつ前回エントリー値から、しきい値以上の差分が存在する
					if (distance >= this.pipsAdditionalThreshold) {
						// オーダーコマンド
						IEngine.OrderCommand command = (state == AnalyzeState.ENTRY_UP) ? IEngine.OrderCommand.SELL : IEngine.OrderCommand.BUY;

						// リトライ回数を加算する
						retryCountMap.put(instrument, ++retryCount);

						// ナンピンロット＝初期ロット＋（増分×リトライ回数）
						double additionalLot = initLot + (initLot * retryCount * this.lotAdditional);

						printDebugLog(instrument, state, bidBar.getTime(), "Retry:" + retryCount + " / " + this.retryCountAdditional);

						IOrder order = null;

						// エントリーに成功した場合は、オーダーリストに追加する
						if ((order = entry(instrument, state, bidBar.getTime(), command, additionalLot, close)) != null) {
							orderList.add(order);
						}
					}
				}
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
	private IOrder entry(Instrument instrument, AnalyzeState state, long time, OrderCommand command, double amount, double close) throws JFException {

		// エントリーする
		IOrder order = engine.submitOrder(
				getLabel(instrument, command, amount),
				instrument,
				command,
				amount, // 注文する量
				0, // 価格。０の場合はJForexで確認できた最新価格
				MAX_SLIPPAGE);

		// オーダーが受理されるまで待機する
		order.waitForUpdate(TIMEOUT_ORDER_UPDATE, State.FILLED, State.CANCELED);

		// 正しく受理されている場合
		if (order.getState() == State.FILLED) {
			printDebugLog(instrument, state, time, "order filled. open price=" + order.getOpenPrice() + ", lots=" + order.getAmount() * 1000);
			return order;
		}
		// キャンセルされた場合
		else {
			printLog(instrument, state, time, "order canceled.");
			return null;
		}
	}

	/**
	 * ポジション保持時間を取得する。
	 * 
	 * @param period 時間足
	 * @return ポジション保持時間
	 */
	private long getPositionTimeLimit(Period period) {
		return maxPeriod * period.getInterval();
	}

	/**
	 * 共通情報をログ出力する。
	 * 
	 * @param instrument 通貨ペア
	 * @param state 状態
	 * @param time 時刻
	 * @param bbands ボリンジャーバンド情報
	 * @param fractal フラクタル解析情報
	 */
	private void printCommonInfoLog(Instrument instrument, AnalyzeState state, long time, BollingerBandsAnalyzer bbands, FractalAnalyzer fractal) {
		printDebugLog(
				instrument,
				state,
				time,
				"Equity:" + account.getEquity(),
				"BollingerBands:" + bbands.toSimpleString(),
				"Fractal:" + fractal.toSimpleString());
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
		if (state != AnalyzeState.ENTRY_UP && state != AnalyzeState.ENTRY_DOWN) {
			return;
		}

		List<IOrder> orderList = orderListMap.get(instrument);

		// 通貨ペアに対応する前回終値を取得する
		if (!closeMap.containsKey(instrument)) {

			closeMap.put(instrument, context.getHistory().getBar(instrument, period, OfferSide.BID, 0).getClose());
		}
		double close = closeMap.get(instrument);

		// 通貨ペアに対応するボリンジャーバンドを取得する
		double[] bbands = getBollingerBands(instrument, period);

		// 中間バンドにタッチ
		if ((close > bbands[1] && bbands[1] >= tick.getBid()) || (close < bbands[1] && bbands[1] <= tick.getBid())) {
			// クローズし、各種情報を初期化する
			closeAndInit(instrument, state);
			return;
		}

		// それ以外の場合は損切りの判定を行う
		for (IOrder order : orderList) {
			if (order.getState() != State.FILLED) {
				continue;
			}

			double open = order.getOpenPrice();
			double distance = (state == AnalyzeState.ENTRY_UP) ?
					tick.getBid() - open :
					open - tick.getBid();

			// 損切りするか否か
			boolean lossCut = distance >= (pipsLossCut * instrument.getPipValue());

			// セーフモードCの損切り判定
			boolean lossCutSafeModeC = isSafeModeC &&
					(state == AnalyzeState.ENTRY_UP) ? open <= bbands[1] : open >= bbands[1];

			// 損切りラインを超えた場合
			if (lossCut) {
				// セーフモードＡの場合はすべてクローズして終了する
				if (isSafeModeA) {
					printLog(instrument, state, tick.getTime(), "safe mode A: close all orders. entry disabled.");
					closeAndInit(instrument, state, true);
					stateMap.put(instrument, AnalyzeState.NO_ENTRY);
					break;
				}
				else {
					// オーダーをクローズする
					order.close();
					printClosedOrder(instrument, state, order, true);
				}
			}
			// セーフモードＣでセンターバンドがエントリー値に追いついた場合
			else if (lossCutSafeModeC) {
				printLog(instrument, state, tick.getTime(), "safe mode C: close order.");
				// オーダーをクローズする
				order.close();
				printClosedOrder(instrument, state, order, true);
			}
			// 損切りラインを超えない場合は、以降も超えていないはずなので終了
			else {
				break;
			}
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
	private String getLabel(Instrument instrument, OrderCommand command, double currency) {
		return instrument.name() + "_" + command.name() + "_" + (int) (currency * 1000) + "Lots_" + sdfLabel.format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
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
	 * @return ボリンジャーバンド。<br>
	 * [0]：上部バンド<br>
	 * [1]：中間バンド<br>
	 * [2]：下部バンド
	 * @throws JFException ボリンジャーバンドの取得に失敗した場合
	 */
	private double[] getBollingerBands(Instrument instrument, Period period) throws JFException {
		// ボリンジャーバンドを取得して返却する
		return indicators.bbands(
				instrument,
				period,
				OfferSide.BID,
				AppliedPrice.CLOSE,
				20,
				2.0, // 上部バンドのσ
				2.0, // 下部バンドのσ
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

		// リトライカウントを初期化する
		retryCountMap.remove(instrument);

		// バンドタッチ後カウンタを初期化する
		offsetMap.remove(instrument);

		// クローズ期限を初期化する
		closeLimitMap.remove(instrument);
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
	 * 取引対象とするか否かを判定する。
	 * 
	 * @param time 時刻
	 * @param goodTillTime ポジション保持時間
	 * @return 判定結果。<br>true：取引対象、false：取引対象外
	 */
	private boolean isActiveTime(long time, long goodTillTime) {
		// 回避フラグとセーフモードがOFFであれば、常に取引対象とする
		if (!isAvoidStartEnd && !isSafeModeB) {
			return true;
		}

		// 週初、月初チェック用
		Calendar calHead = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calHead.setTimeInMillis(time);

		// 週末、月末チェック用
		Calendar calFoot = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calHead.setTimeInMillis((isSafeModeB ? time + goodTillTime : time));

		// 曜日を取得する
		int weekdayHead = calHead.get(Calendar.DAY_OF_WEEK);
		int weekdayFoot = calFoot.get(Calendar.DAY_OF_WEEK);

		// 回避フラグがオンの場合
		if (isAvoidStartEnd) {
			// 月、金は対象外とする
			if (weekdayHead == Calendar.MONDAY || weekdayFoot == Calendar.FRIDAY) {
				return false;
			}
		}
		else {
			// 土は対象外とする
			if (weekdayFoot == Calendar.SATURDAY) {
				return false;
			}
		}

		// 1月の場合
		if (calHead.get(Calendar.MONTH) == Calendar.JANUARY) {
			int day = calHead.get(Calendar.DAY_OF_MONTH);

			// 回避フラグがオンの場合
			if (isAvoidStartEnd) {
				// 1、2は元日対応として対象外とする
				if (day == 1 || day == 2) {
					return false;
				}
			}
			// 回避フラグがオフの場合
			else {
				// 元日のみ対象外とする
				if (day == 1) {
					return false;
				}
			}
		}
		// 12月の場合
		else if (calHead.get(Calendar.MONTH) == Calendar.DECEMBER) {
			int dayHead = calHead.get(Calendar.DAY_OF_MONTH);
			int dayFoot = calFoot.get(Calendar.DAY_OF_MONTH);

			// 回避フラグがオンの場合
			if (isAvoidStartEnd) {
				// 12/24、25、26はクリスマス対応として取引対象外とする
				if (dayFoot == 24 || dayHead == 25 || dayHead == 26) {
					return false;
				}
			}
			// 回避フラグがオフの場合
			else {
				// クリスマスのみ対象外とする
				if (dayFoot == 25) {
					return false;
				}
			}
		}

		// 回避フラグがオフであれば、対象とする
		if (!isAvoidStartEnd) {
			return true;
		}

		// 月初
		Calendar start = (Calendar) calHead.clone();
		start.set(Calendar.DAY_OF_MONTH, 1);

		// 月末
		Calendar end = (Calendar) calHead.clone();
		end.set(Calendar.DAY_OF_MONTH, calHead.getActualMaximum(Calendar.DATE));

		// １日を設定する
		int startDay = start.get(Calendar.DAY_OF_WEEK);

		while (startDay == Calendar.SUNDAY || startDay == Calendar.SATURDAY) {
			start.add(Calendar.DAY_OF_MONTH, 1);
			startDay = start.get(Calendar.DAY_OF_WEEK);
		}

		int endDay = end.get(Calendar.DAY_OF_WEEK);

		while (endDay == Calendar.SUNDAY || endDay == Calendar.SATURDAY) {
			end.add(Calendar.DAY_OF_MONTH, -1);
			endDay = end.get(Calendar.DAY_OF_WEEK);
		}

		// 月初、月末でなければtrue、そうでなければfalseを返却する
		return calHead.get(Calendar.DAY_OF_MONTH) != start.get(Calendar.DAY_OF_MONTH) && calFoot.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH);
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
		public String toSimpleString() {
			return (isUpFractal() ? "⇓" : "") + (isDownFractal() ? "⇑" : "");
		}
	}
}