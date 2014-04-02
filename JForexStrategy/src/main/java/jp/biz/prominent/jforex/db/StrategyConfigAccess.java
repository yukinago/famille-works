package jp.biz.prominent.jforex.db;

import jp.biz.prominent.jforex.entity.StrategyConfig;
import jp.biz.prominent.jforex.mapper.StrategyConfigMapper;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;

/**
 * ストラテジー設定にアクセスするためのクラス。
 * 
 * @author famille
 */
public class StrategyConfigAccess {

	/** ストラテジー設定アクセス用マッパー */
	@Inject
	private StrategyConfigMapper mapper;

	/**
	 * ストラテジー設定テーブルが存在しない場合は作成する。
	 */
	public void createTable() {
		mapper.createTable();
	}

	/**
	 * ストラテジー設定を取得する。
	 * 
	 * @return ストラテジー設定
	 */
	public StrategyConfig select() {
		return mapper.select();
	}

	/**
	 * ストラテジー設定を保存する。
	 * 
	 * @param config ストラテジー設定
	 * @return 登録件数
	 */
	@Transactional
	public int insert(StrategyConfig config) {
		return mapper.insert(config);
	}

	/**
	 * ストラテジー設定を削除する。
	 * 
	 * @return 削除件数
	 */
	@Transactional
	public int delete() {
		return mapper.delete();
	}
}
