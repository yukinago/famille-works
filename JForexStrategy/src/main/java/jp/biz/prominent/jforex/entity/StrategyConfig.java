package jp.biz.prominent.jforex.entity;

import static com.dukascopy.api.Instrument.*;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

/**
 * ストラテジー設定情報を表すエンティティ。
 * 
 * @author famille
 */
@Data
public class StrategyConfig {
	/** 通貨ペア（カンマ区切り） **/
	private String instruments = EURUSD + "," + GBPUSD + "," + EURJPY + "," + USDJPY;

	/** バーピリオド */
	private String barPeriods = Period.FIVE_MINS.toString();

	/** 残高100$ごとのロット数 */
	private Double lotPer100 = 0.5;

	/** 最大ロット数 */
	private Double lotMax = 10000.0;

	/** エントリーしきい値（PIPS） */
	private Integer pipsEntryThreshold = 4;

	/** 買い（売り）増し増分ロット数 */
	private Double lotAdditional = 2.0;

	/** 買い（売り）増ししきい値（PIPS） */
	private Integer pipsAdditionalThreshold = 4;

	/** 買い（売り）増しリトライ回数 */
	private Integer retryCountAdditional = 6;

	/** 損切りしきい値（最大リトライ後からのPIPS） */
	private Integer pipsLossCut = 15;

	/**
	 * 通貨ペアのセットを取得する。
	 * 
	 * @return 通貨ペアのセット
	 */
	public Set<Instrument> getInstrumentSet() {

		Set<Instrument> instrumentSet = new HashSet<Instrument>();

		String[] instruments = StringUtils.split(getInstruments(), ",");

		if (ArrayUtils.contains(instruments, Instrument.EURUSD.toString())) {
			instrumentSet.add(Instrument.EURUSD);
		}
		if (ArrayUtils.contains(instruments, Instrument.GBPUSD.toString())) {
			instrumentSet.add(Instrument.GBPUSD);
		}
		if (ArrayUtils.contains(instruments, Instrument.EURJPY.toString())) {
			instrumentSet.add(Instrument.EURJPY);
		}
		if (ArrayUtils.contains(instruments, Instrument.USDJPY.toString())) {
			instrumentSet.add(Instrument.USDJPY);
		}

		return instrumentSet;
	}

	/**
	 * 時間足を取得する。
	 * 
	 * @return 時間足
	 */
	public Period getPeriod() {
		return Period.valueOfToString(getBarPeriods());
	}

	/**
	 * ストラテジー設定の文字列表現を取得する。
	 * 
	 * @return ストラテジー設定文字列表現
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
