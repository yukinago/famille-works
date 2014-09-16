package jp.biz.prominent.app.com.db;

import java.io.File;
import java.io.IOException;

import jp.biz.prominent.app.com.db.constants.TestConstants;
import jp.biz.prominent.app.com.inject.InjectionUtils;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersionUpAccessTest {

	private static VersionUpAccess target;

	private static UserAccess userAccess;
	private static FollowerAccess followerAccess;
	private static TweetAccess tweetAccess;

	@BeforeClass
	public static void init() {
		InjectionUtils.setConfigFile(TestConstants.CONFIG_FILE_VERSION_UP);

		target = InjectionUtils.getInstance(VersionUpAccess.class);

		// テーブル作成用
		userAccess = InjectionUtils.getInstance(UserAccess.class);
		followerAccess = InjectionUtils.getInstance(FollowerAccess.class);
		tweetAccess = InjectionUtils.getInstance(TweetAccess.class);

		try {
			FileUtils.copyFile(
					new File("src/test/resources/userdata_test.sqlite3"),
					new File("userdata_test.sqlite3")
					);

			// 新テーブル作成
			userAccess.createTable();
			followerAccess.createTable();
			tweetAccess.createTable();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void versionUp() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		target.versionUp();
		// Then
	}

}
