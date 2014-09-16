package jp.biz.prominent.app.com.db;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.db.constants.TestConstants;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.UserEntity;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserAccessTest {

	private static UserAccess target;

	private static UserEntity before;
	private static UserEntity before2;
	private static UserEntity after;

	@BeforeClass
	public static void init() {
		// テスト用のコンフィグに切り替える
		InjectionUtils.setConfigFile(TestConstants.CONFIG_FILE);

		target = InjectionUtils.getInstance(UserAccess.class);

		before = new UserEntity();

		before.setUid(1L);
		before.setName("ユーザー１");
		before.setImageUrl("<html><body>before</body></html>");
		before.setConsumerKey("conkey1");
		before.setConsumerSecret("consec1");
		before.setAccessToken("acctoken1");
		before.setAccessTokenSecret("acctokensec1");
		before.setKeyword1("key1-1");
		before.setKeyword2("key2-1");
		before.setKeyword3("key3-1");
		before.setDistance("100");
		before.setLongitude("10");
		before.setLatitude("20");
		before.setSearchType(0);
		before.setFollow(Constants.FLG_ON);
		before.setRefollow(Constants.FLG_ON);
		before.setDuplicate(Constants.FLG_ON);
		before.setThresholdNoFollow(7);
		before.setThresholdNoTweet(2);
		before.setDailyFollowMax(50);
		before.setFollowDate(DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd"));
		before.setFollowCount(10);
		before.setInterval(10);
		before.setRange(10);

		before2 = new UserEntity();

		before2.setUid(2L);
		before2.setName("ユーザー２");
		before2.setImageUrl("<html><body>before2</body></html>");
		before2.setConsumerKey("conkey2");
		before2.setConsumerSecret("consec2");
		before2.setAccessToken("acctoken2");
		before2.setAccessTokenSecret("acctokensec2");
		before2.setKeyword1("key1-2");
		before2.setKeyword2("key2-2");
		before2.setKeyword3("key3-2");
		before2.setDistance("200");
		before2.setLongitude("20");
		before2.setLatitude("40");
		before2.setSearchType(0);
		before2.setFollow(Constants.FLG_ON);
		before2.setRefollow(Constants.FLG_ON);
		before2.setDuplicate(Constants.FLG_ON);
		before2.setThresholdNoFollow(14);
		before2.setThresholdNoTweet(4);
		before2.setDailyFollowMax(100);
		before2.setFollowDate(DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd"));
		before2.setFollowCount(20);
		before2.setInterval(20);
		before2.setRange(20);

		after = new UserEntity();

		after.setUid(1L);
		after.setName("ユーザー１'");
		after.setImageUrl("<html><body>after</body></html>");
		after.setConsumerKey("conkey1'");
		after.setConsumerSecret("consec1'");
		after.setAccessToken("acctoken1'");
		after.setAccessTokenSecret("acctokensec1'");
		after.setKeyword1("key1-1'");
		after.setKeyword2("key2-1'");
		after.setKeyword3("key3-1'");
		after.setDistance("105");
		after.setLongitude("15");
		after.setLatitude("25");
		after.setSearchType(1);
		after.setFollow(Constants.FLG_OFF);
		after.setRefollow(Constants.FLG_OFF);
		after.setDuplicate(Constants.FLG_OFF);
		after.setThresholdNoFollow(8);
		after.setThresholdNoTweet(3);
		after.setDailyFollowMax(51);
		after.setFollowDate(DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd"));
		after.setFollowCount(11);
		after.setInterval(11);
		after.setRange(11);

		target.createTable();
	}

	@Before
	public void setup() {
		target.insert(before);
	}

	@After
	public void tearDown() {
		target.deleteByUid(before.getUid());
		target.deleteByUid(before2.getUid());
	}

	@Test
	public void type() throws Exception {
		// TODO JUnit Helper による自動生成
		assertThat(UserAccess.class, notNullValue());
	}

	@Test
	public void instantiation() throws Exception {
		// TODO JUnit Helper による自動生成
		UserAccess target = new UserAccess();
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
	public void selectAll() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		UserEntity actual = target.selectAll().get(0);
		// Then
		assertThat(actual, is(equalTo(before)));
	}

	@Test
	public void selectByUid() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		Long id = before.getUid();
		// When
		UserEntity actual = target.selectByUid(id);
		// Then
		assertThat(actual, is(equalTo(before)));
	}

	@Test
	public void insertUser() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		UserEntity userEntity = before2;
		// When
		int actual = target.insert(userEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void update() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		UserEntity userEntity = after;
		// When
		int actual = target.update(userEntity);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void deleteByUid() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		int actual = target.deleteByUid(before.getUid());
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

}
