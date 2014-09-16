package jp.biz.prominent.app.com.inject;

import jp.biz.prominent.app.com.ToolInformation;
import lombok.Setter;

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
	private static String configFile = ToolInformation.isLiteVersion() ? "mybatis-config-lite.xml" : "mybatis-config.xml";

	/** インジェクション用 */
	private static Injector injector;

	/**
	 * インジェクションしたインスタンスを取得する。
	 * 
	 * @param type クラス
	 * @return インスタンス
	 */
	public static <T> T getInstance(Class<T> type) {
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
