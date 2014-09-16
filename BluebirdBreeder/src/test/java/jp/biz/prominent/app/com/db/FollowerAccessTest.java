package jp.biz.prominent.app.com.db;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.db.constants.TestConstants;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.FollowerEntity;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FollowerAccessTest {

	private static FollowerAccess target;

	private static FollowerEntity before;
	private static FollowerEntity before2;
	private static FollowerEntity other;
	private static FollowerEntity after;

	@BeforeClass
	public static void init() {

		// テスト用のコンフィグに切り替える
		InjectionUtils.setConfigFile(TestConstants.CONFIG_FILE);

		target = InjectionUtils.getInstance(FollowerAccess.class);

		before = new FollowerEntity();
		before.setUid(1L);
		before.setUnfollow("0");
		before.setStatus(Constants.FOLLOW_NOT);
		before.setFid(10L);
		before.setName("フォロー対象ユーザ１");
		before.setImageUrl("<html><body>before</body></html>");
		before.setFollowTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));

		before2 = new FollowerEntity();
		before2.setUid(1L);
		before2.setUnfollow("0");
		before2.setStatus(Constants.FOLLOW_NOT);
		before2.setFid(11L);
		before2.setName("フォロー対象ユーザ２");
		before2.setImageUrl("<html><body>before2</body></html>");
		before2.setFollowTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));

		other = new FollowerEntity();
		other.setUid(2L);
		other.setUnfollow("1");
		other.setStatus(Constants.FOLLOW_NOT);
		other.setFid(10L);
		other.setName("フォロー対象ユーザ１");
		other.setImageUrl("<html><body>other</body></html>");
		other.setFollowTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));

		after = new FollowerEntity();
		after.setUid(1L);
		after.setUnfollow("1");
		after.setStatus(Constants.FOLLOW_LOCAL);
		after.setFid(10L);
		after.setName("フォロー対象ユーザ１'");
		after.setImageUrl("<html><body>after</body></html>");
		after.setFollowTime(DateFormatUtils.format(Calendar.getInstance(), "yyyy/MM/dd HH:mm:ss"));

		target.createTable();
	}

	@Before
	public void setup() {
		target.insert(before);
		target.insert(other);
	}

	@After
	public void tearDown() {
		target.deleteByUid(before.getUid());
		target.deleteByUid(other.getUid());
	}

	@Test
	public void type() throws Exception {
		// TODO JUnit Helper による自動生成
		assertThat(FollowerAccess.class, notNullValue());
	}

	@Test
	public void instantiation() throws Exception {
		assertThat(target, notNullValue());
	}

	@Test
	public void createTable() throws Exception {
		try {
			// Given
			// When
			target.createTable();
			// Then
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void insert() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given

		// When
		int actual = target.insert(before2);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void selectByUid() throws Exception {
		// Given
		Long uid = before.getUid();
		// When
		FollowerEntity actual = target.selectByUid(uid).get(0);
		// Then
		assertEquals(before.getUid(), actual.getUid());
		assertEquals(before.getUnfollow(), actual.getUnfollow());
		assertEquals(before.getStatus(), actual.getStatus());
		assertEquals(before.getFid(), actual.getFid());
		assertEquals(before.getName(), actual.getName());
		assertEquals(before.getImageUrl(), actual.getImageUrl());
		assertEquals(before.getFollowTime(), actual.getFollowTime());
	}

	@Test
	public void update() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		int actual = target.update(after);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void delete() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		int actual = target.delete(after);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void deleteByUid() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		// When
		int actual = target.deleteByUid(after.getUid());
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void deleteExistUnFollow() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		Long uid = before.getUid();
		// When
		int actual = target.deleteExistUnFollow(uid);
		// Then
		int expected = 1;
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void deleteExistUnFollow_2() throws Exception {
		// TODO JUnit Helper による自動生成
		// Given
		Long uid = other.getUid();
		// When
		int actual = target.deleteExistUnFollow(uid);
		// Then
		int expected = 0;
		assertThat(actual, is(equalTo(expected)));
	}

}
