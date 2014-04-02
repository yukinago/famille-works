package jp.biz.prominent.jforex.mapper;

import jp.biz.prominent.jforex.entity.StrategyConfig;

import org.apache.ibatis.annotations.Param;

/**
 * ストラテジー設定のマッピングインターフェイス。
 * 
 * @author famille
 */
public interface StrategyConfigMapper {

	/**
	 * テーブルが存在しない場合は作成する。
	 */
	void createTable();

	/**
	 * ストラテジー設定を取得する。
	 * 
	 * @return ストラテジー設定
	 */
	StrategyConfig select();

	/**
	 * ストラテジー設定を追加する。
	 * 
	 * @param config ストラテジー設定
	 * @return 更新件数
	 */
	int insert(@Param("config") StrategyConfig config);

	/**
	 * ストラテジー設定を削除する。
	 * 
	 * @return 削除件数
	 */
	int delete();
}
