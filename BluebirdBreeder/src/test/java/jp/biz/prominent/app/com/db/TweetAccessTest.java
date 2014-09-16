package jp.biz.prominent.app.com.db;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.db.constants.TestConstants;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.TweetEntity;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TweetAccessTest {

	private static TweetAccess target;

	private static TweetEntity before;
	private static TweetEntity before2;
	private static TweetEntity after;

	@BeforeClass
	public static void init() {
		// テスト用のコンフィグに切り替える
		InjectionUtils.setConfigFile(TestConstants.CONFIG_FILE);

		target = InjectionUtils.getInstance(TweetAccess.class);

		before = new TweetEntity();
		before.setUid(1L);
		before.setTid(10);
		before.setCreatedTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));
		before.setSelected(Constants.FLG_ON);
		before.setTemplate("テンプレート");

		before2 = new TweetEntity();
		before2.setUid(1L);
		before2.setTid(20);
		before2.setCreatedTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));
		before2.setSelected(Constants.FLG_OFF);
		before2.setTemplate("テンプレート２");

		after = new TweetEntity();
		after.setUid(1L);
		after.setTid(10);
		after.setCreatedTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));
		after.setSelected(Constants.FLG_OFF);
		after.setTemplate("テンプレート’");

		target.createTable();
	}

	@Before
	public void setup() {
		target.insert(before);
	}

	@After
	public void tearDown() {
		target.deleteByUid(before.getUid());
	}

	@Test
	public void type() throws Exception {
		// TODO JUnit Helper による自動生成
		assertThat(TweetAccess.class, notNullValue());
	}

	@Test
	public void instantiation() throws Exception {
		// TODO JUnit Helper による自動生成

		assertThat(target, notNullValue());
	}

	@Test
	public void createTable() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given

		// When
		target.createTable();
		// Then
	}

	@Test
	public void selectByUid() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		Long uid = before.getUid();
		// When
		TweetEntity actual = target.selectByUid(uid).get(0);
		// Then
		TweetEntity expected = before;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void insert() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		TweetEntity tweetEntity = before2;
		// When
		int actual = target.insert(tweetEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void update() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		TweetEntity tweetEntity = after;
		// When
		int actual = target.update(tweetEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void updateTweetId() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		target.insert(before2);
		TweetEntity tweetEntity = before;
		// When
		int actual = target.updateTweetId(tweetEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void delete() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		target.insert(before2);
		TweetEntity tweetEntity = before;
		// When
		int actual = target.delete(tweetEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void deleteByUid() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		target.insert(before2);
		Long uid = before.getUid();
		// When
		int actual = target.deleteByUid(uid);
		// Then
		int expected = 2;
		assertThat(actual, is(equalTo(expected)));
	}

}
