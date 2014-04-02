package jp.co.famille.util.di;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.guice.XMLMyBatisModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * インジェクション関連のユーティリティ・クラス。
 * 
 * @author famille
 */
public class InjectionUtils {

	@Setter
	@Getter
	/** コンフィグファイル */
	private static String configFile;

	/** インジェクション用 */
	private static Injector injector;

	/**
	 * インジェクションしたインスタンスを取得する。
	 * 
	 * @param type クラス
	 * @return インスタンス
	 */
	public static <T> T getInstance(Class<T> type) {
		// コンフィグファイル未設定の場合
		if (StringUtils.isEmpty(configFile)) {
			throw new UnsupportedOperationException("コンフィグファイルが設定されていません。\nInjectionUtils.setConfigFile(String fileName)で設定してください。");
		}

		// インジェクターが生成されていない場合
		if (injector == null) {
			injector = Guice.createInjector(new XMLMyBatisModule() {
				@Override
				protected void initialize() {
					setEnvironmentId("development");
					setClassPathResource(configFile);
				}
			});
		}

		return injector.getInstance(type);
	}
}
