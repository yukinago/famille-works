package jp.biz.prominent.app.com;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.biz.prominent.app.com.Constants.FollowResult;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import jp.biz.prominent.app.gui.Header.AutoFollowTask;
import jp.biz.prominent.app.gui.Header.AutoTwistTask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * ユーザ情報
 *
 * @author famille
 */
@CommonsLog
public class UserInfo {

	/** ユーザー情報 */
	private User user = null;

	/** ツィータ情報 */
	private Twitter twitter = null;

	@Getter
	@Setter
	/** ユーザーDB情報 */
	private UserEntity userEntity = null;

	@Getter
	@Setter
	/** 自動フォローTask */
	private AutoFollowTask followTask = null;

	@Getter
	@Setter
	/** 自動ツィートTask */
	private AutoTwistTask twistTask = null;

	/** １頁の結果件数 */
	private static final int RES_PER_PAGE = 80;

	private Log accessLog = LogFactory.getLog("access");

	/**
	 * コンストラクタ
	 */
	public UserInfo() {
		twitter = new TwitterFactory().getInstance();
	}

	/**
	 * インスタンス取得
	 *
	 * @return
	 */
	public static UserInfo getInstance() {
		return new UserInfo();
	}

	/**
	 * ユーザID取得
	 *
	 * @return
	 */
	public Long getId() {
		return user.getId();
	}

	/**
	 * ユーザニックネーム取得
	 *
	 * @return
	 */
	public String getName() {
		return user.getName();
	}

	/**
	 * ユーザアイコンURL取得
	 *
	 * @param width
	 * @param height
	 * @return
	 */
	public String getImageUrl() {
		return user.getProfileImageURL().toString();
	}

	/**
	 * ユーザ認証
	 *
	 * @param mainFrame
	 * */
	public boolean verify(MainFrame mainFrame) {
		log.info("ユーザ認証。");

		boolean ret = true;
		try {
			twitter.setOAuthConsumer(userEntity.getConsumerKey(), userEntity.getConsumerSecret());
			twitter.setOAuthAccessToken(new AccessToken(userEntity.getAccessToken(), userEntity.getAccessTokenSecret()));

			user = twitter.verifyCredentials();
			accessLog.info("ユーザ認証：verifyCredentials");

			RateLimitStatus rs = user.getRateLimitStatus();
			log.info("getLimit=" + rs.getLimit());
			log.info("getRemaining=" + rs.getRemaining());
			log.info("getResetTimeInSeconds=" + rs.getResetTimeInSeconds());
			log.info("getSecondsUntilReset=" + rs.getSecondsUntilReset());

			// アイコン画像の保存
			String imgPath = "./userIcons/" + this.getId() + this.getImageUrl().substring(this.getImageUrl().lastIndexOf("."));
			CommonUtil.saveImg(this.getImageUrl(), imgPath);

			// ユーザー情報更新
			userEntity.setName(this.getName());
			userEntity.setImageUrl(imgPath.substring(1));

			// DB情報更新
			UserAccess user = InjectionUtils.getInstance(UserAccess.class);
			user.update(userEntity);
		}
		catch (TwitterException e) {
			ret = false;
			log.error("認証エラー");
			UserUtil.checkException(e, mainFrame);

		}
		catch (IllegalArgumentException e) {
			ret = false;
			log.error("認証エラー");
		}

		return ret;
	}

	/**
	 * ユーザ情報の更新
	 *
	 * @param imgPath アイコンファイルパス
	 * @param nickName ニックネーム
	 * @param mainFrame
	 */
	public void updateUserData(String imgPath, String nickName, MainFrame mainFrame) {
		log.info("ユーザ情報の更新。");
		try {
			File imgFile = new File(imgPath);

			// Img指定の場合、アイコン更新
			if ("".equals(imgPath.trim()) == false) {
				user = twitter.updateProfileImage(imgFile);
				accessLog.info("ユーザアイコン更新：updateProfileImage");

				// アイコン画像保存
				imgPath = "/userIcons/" + this.getId() + imgPath.substring(imgPath.lastIndexOf("."));
				CommonUtil.fileCopy(imgFile, System.getProperty("user.dir") + imgPath);

				// ユーザー情報更新
				userEntity.setImageUrl(imgPath);

				// 画像保存後の余裕時間
				try {
					Thread.sleep(3000);
				}
				catch (Exception e) {
					//
				}
			}

			// 名称更新
			user = twitter.updateProfile(nickName, null, null, null);
			accessLog.info("ユーザ名称更新：updateProfile");

			// ユーザー情報更新
			userEntity.setName(nickName);

		}
		catch (TwitterException e) {
			log.error("User data update error.");
			UserUtil.checkException(e, mainFrame);
		}
	}

	/**
	 * フレンドを検索する。
	 *
	 * @param mainFrame
	 * @return
	 */
	public List<User> getServerFriendsList(MainFrame mainFrame) {
		log.info("フレンドの検索。");

		List<User> friendsList = new ArrayList<User>();

		try {
			long[] friendsIds = twitter.getFriendsIDs(-1).getIDs();
			accessLog.info("フレンドの検索：getFriendsIDs");

			long[] ids = new long[friendsIds.length];
			if (ids.length > RES_PER_PAGE) {
				ids = new long[RES_PER_PAGE];
			}

			boolean hasDataFlg = false;
			for (int i = 0; i < friendsIds.length; i++) {
				ids[i % RES_PER_PAGE] = friendsIds[i];
				hasDataFlg = true;

				// 頁の最大件数
				if (i % RES_PER_PAGE + 1 == RES_PER_PAGE) {
					// Return up to 100 users
					friendsList.addAll(twitter.lookupUsers(ids));
					accessLog.info("フレンドの検索：lookupUsers");

					hasDataFlg = false;

					if (friendsIds.length > i + 1 + RES_PER_PAGE) {
						ids = new long[RES_PER_PAGE];
					}
					else {
						ids = new long[friendsIds.length - i - 1];
					}
				}
			}

			if (hasDataFlg) {
				// Return up to 100 users
				friendsList.addAll(twitter.lookupUsers(ids));
				accessLog.info("フレンドの検索：lookupUsers");
			}

		}
		catch (TwitterException e) {
			log.error("Follow search error.");
			UserUtil.checkException(e, mainFrame);
		}

		log.info("Server Follow Count :" + friendsList.size());
		return friendsList;
	}

	/**
	 * フォロワー検索
	 *
	 * @param mainFrame
	 * @return
	 */
	public List<User> getFollowerList(MainFrame mainFrame) {
		log.info("フォロワーの検索。");

		List<User> followerList = new ArrayList<User>();

		try {
			long[] followersIds = twitter.getFollowersIDs(user.getId(), -1).getIDs();
			accessLog.info("フォロワーの検索：getFollowersIDs");

			long[] ids = new long[followersIds.length];
			if (ids.length > RES_PER_PAGE) {
				ids = new long[RES_PER_PAGE];
			}

			boolean hasDataFlg = false;
			for (int i = 0; i < followersIds.length; i++) {
				ids[i % RES_PER_PAGE] = followersIds[i];
				hasDataFlg = true;

				// 頁の最大件数
				if (i % RES_PER_PAGE + 1 == RES_PER_PAGE) {
					// Return up to 100 users
					followerList.addAll(twitter.lookupUsers(ids));
					accessLog.info("フォロワーの検索：lookupUsers");

					hasDataFlg = false;

					if (followersIds.length > i + 1 + RES_PER_PAGE) {
						ids = new long[RES_PER_PAGE];
					}
					else {
						ids = new long[followersIds.length - i - 1];
					}
				}
			}

			if (hasDataFlg) {
				// Return up to 100 users
				followerList.addAll(twitter.lookupUsers(ids));
				accessLog.info("フォロワーの検索：lookupUsers");
			}

		}
		catch (TwitterException e) {
			log.error("Follower search error.");
			UserUtil.checkException(e, mainFrame);
		}

		return followerList;
	}

	/**
	 * Tweetの検索
	 *
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param radius
	 * @param longitude
	 * @param latitude
	 * @param andFlg
	 * @param mainFrame
	 * @param exceptObjs
	 * @return
	 */
	public List<User> searchByKeys(String key1, String key2, String key3, double radius, double longitude, double latitude, int searchType, MainFrame mainFrame, List<Long> exceptObjs) throws TwitterException {

		log.info("ツイートの検索。");

		List<Long> keyTweetList = new ArrayList<Long>();
		Query query = new Query();
		query.setCount(RES_PER_PAGE);

		// KEY検索、AND検索の場合
		if (searchType == 3 || searchType == 4) {
			// AND検索の場合
			if (searchType == 4) {
				query.setGeoCode(new GeoLocation(latitude, longitude), radius, Query.KILOMETERS);
			}

			// Key1検索
			query.setQuery(key1);
			this.searchToMax(query, keyTweetList, mainFrame, exceptObjs);

			// 0件の場合
			if (keyTweetList.size() == 0 && "".equals(key2.trim()) == false) {
				// Key2検索
				query.setQuery(key2);
				this.searchToMax(query, keyTweetList, mainFrame, exceptObjs);
			}

			// 0件の場合
			if (keyTweetList.size() == 0 && "".equals(key3.trim()) == false) {
				// Key3検索
				query.setQuery(key3);
				this.searchToMax(query, keyTweetList, mainFrame, exceptObjs);
			}
		}
		// GPS検索
		else {
			// TODO 使用しない 250km以上すると、TwitterがGEO条件を無視される
			query.setGeoCode(new GeoLocation(latitude, longitude), radius, Query.KILOMETERS);
			this.searchToMax(query, keyTweetList, mainFrame, exceptObjs);
		}

		// 対応User検索
		List<User> tweetUserList = new ArrayList<User>();

		long[] ids = new long[keyTweetList.size()];
		if (keyTweetList.size() > RES_PER_PAGE) {
			ids = new long[RES_PER_PAGE];
		}

		boolean hasDataFlg = false;
		for (int i = 0; i < keyTweetList.size(); i++) {
			ids[i % RES_PER_PAGE] = keyTweetList.get(i).longValue();
			hasDataFlg = true;

			// 頁の最大件数
			if (i % RES_PER_PAGE + 1 == RES_PER_PAGE) {
				// Return up to 100 users
				tweetUserList.addAll(twitter.lookupUsers(ids));
				accessLog.info("ツイートの検索：lookupUsers");

				hasDataFlg = false;
				if (keyTweetList.size() > i + 1 + RES_PER_PAGE) {
					ids = new long[RES_PER_PAGE];
				}
				else {
					ids = new long[keyTweetList.size() - i - 1];
				}
			}
		}
		if (hasDataFlg) {
			// Return up to 100 users
			tweetUserList.addAll(twitter.lookupUsers(ids));
			accessLog.info("ツイートの検索：lookupUsers");
		}

		// ID順にソートする
		Collections.sort(tweetUserList, new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				return new Long(o1.getId()).compareTo(o2.getId());
			}
		});

		return tweetUserList;
	}

	/**
	 * 最大件数まで繰り返し検索
	 *
	 * @param maxResult
	 * @param query
	 * @param keyTweetList
	 * @param mainFrame
	 * @param exceptObjs
	 * @throws TwitterException
	 */
	private void searchToMax(Query query, List<Long> keyTweetList, MainFrame mainFrame, List<Long> exceptObjs) {

		QueryResult result = null;
		int pageNo = 1;

		while (query != null) {
			try {
				result = twitter.search(query);
				accessLog.info("ツイートの検索：search page" + pageNo);

				// 検索結果保存
				if (result != null && result.getTweets().size() > 0) {
					for (Status status : result.getTweets()) {
						// 重複排除、ローカルフォロー済とフォロー禁止は対象外、サーバ側フォロー済は対象外
						if (keyTweetList.contains(status.getUser().getId()) == false && exceptObjs.contains(status.getUser().getId()) == false) {
							keyTweetList.add(status.getUser().getId());

							// TODO MAX制限、最大2000件表示
							if (keyTweetList.size() == 2000) {
								return;
							}
						}
					}
				}

				// 次のページを検索し続ける
				query = result.nextQuery();
				pageNo++;

			}
			catch (TwitterException e) {
				log.error("Follow key search error.");
				UserUtil.checkException(e, mainFrame);
			}
		}
	}

	/**
	 * フォロー実施
	 *
	 * @param id
	 * @param name
	 * @param userEntity
	 * @param followerNum
	 * @param followNum
	 * @param followBackFlg
	 * @return
	 * @throws TwitterException
	 */
	public FollowResult doFollow(Long id, String name, UserEntity userEntity, int followerNum, int followNum, boolean followBackFlg) throws TwitterException {
		log.info("フォロー実施。" + userEntity.getName() + "[" + name + "]");

		// フォロー数の上限は2001件 または フォロワー数×1.1件 のどちらか多い方
		if (followNum >= 2001 && followNum >= followerNum * 1.1) {
			return FollowResult.USER_COUNT_OVER;
		}

		int followCnt = userEntity.getFollowCount();
		String sysDate = CommonUtil.isSameDay(userEntity.getFollowDate());

		// 同じ日付の場合、1日のフォロー数上限達する場合(フォロー返しは対象外)
		if (sysDate == null && followCnt >= userEntity.getDailyFollowMax() && followBackFlg == false) {
			return FollowResult.DAY_COUNT_OVER;
		}

		// 同じ日付の場合
		if (sysDate == null) {
			sysDate = userEntity.getFollowDate();
		}
		// 違う日付の場合
		else {
			followCnt = 0;
		}

		// ・id指定したユーザーをフォロー
		// ・フォロー済みのユーザーを再フォローしようとるするとHTTPコード403を返す
		// ・フォロー制限を超えてフォローをした場合HTTPコード403を返す
		Object response = twitter.createFriendship(id);

		if (response instanceof User) {
			accessLog.info("フォロー実施：createFriendship " + userEntity.getName() + "[" + name + "]");
		}
		else {
			accessLog.info("フォロー実施：createFriendship " + userEntity.getName() + "[" + name + "] 失敗[" + response + "]");
		}

		// 1日分のフォロー件数カウント
		followCnt = followCnt + 1;
		userEntity.setFollowCount(followCnt);
		userEntity.setFollowDate(sysDate);

		Map<String, RateLimitStatus> limitMap = twitter.getRateLimitStatus();
		Iterator<String> keys = limitMap.keySet().iterator();
		RateLimitStatus rateLimitStatus = null;
		String keyStr = null;
		boolean limitFlg = false;
		while (keys.hasNext()) {
			keyStr = keys.next();
			rateLimitStatus = limitMap.get(keyStr);
			// twitter API制限の場合、リミット残数０
			if (rateLimitStatus.getRemaining() == 0) {
				log.info("Twitter API制限[" + keyStr + "]が到達しましたので、一旦処理停止します。");
				limitFlg = true;
			}
		}

		if (limitFlg) {
			return FollowResult.TWITTER_API_LIMIT;
		}

		return FollowResult.NORMAL;
	}

	/**
	 * アンフォロー実施
	 *
	 * @param id
	 * @throws TwitterException
	 */
	public void unFollow(Long id, String name) throws TwitterException {
		log.info("アンフォロー。" + userEntity.getName() + "[" + name + "]");

		// ・id指定したユーザーをアンフォロー
		User response = twitter.destroyFriendship(id);
		if (response instanceof User) {
			accessLog.info("アンフォロー：destroyFriendship " + userEntity.getName() + "[" + name + "]");
		}
		else {
			accessLog.info("アンフォロー：destroyFriendship " + userEntity.getName() + "[" + name + "] 失敗[" + response + "]");
		}
	}

	/**
	 * ツィート実施
	 *
	 * @param message
	 */
	public void doTweet(String message) throws TwitterException {
		log.info("ツィート実施。メッセージ=" + message);

		// ・140字以内のURLエンコードされたテキストを投稿
		// ・一日1000件まで
		twitter.updateStatus(message);
		accessLog.info("ツィート実施：updateStatus");
	}

	/**
	 * 自動アンフォロー判定
	 *
	 * @param id
	 * @param delDays
	 * @param delWeeks
	 * @return
	 * @throws TwitterException
	 */
	public boolean isUnFollow(Long id, String followTime, int delDays, int delWeeks) throws TwitterException {
		log.info("自動アンフォロー判定。");

		boolean unFollowFlg = false;

		// フォロー時間が○日超え、且つ、フォロワーとならない場合
		unFollowFlg = CommonUtil.isPastTime(followTime, delDays) && !twitter.showFriendship(twitter.getId(), id).isSourceFollowedByTarget();
		accessLog.info("自動アンフォロー判定：showFriendship");

		// アンフォロー対象の場合
		if (unFollowFlg) {
			return unFollowFlg;
		}

		// ツィートがある場合、
		ResponseList<Status> statusList = twitter.getUserTimeline(id);
		accessLog.info("自動アンフォロー判定：getUserTimeline");
		if (statusList.size() > 0) {
			// 最新ツィート時間が○週間超え、且つ、フォロー時間が○週間超えの場合
			unFollowFlg = CommonUtil.isPastTime(statusList.get(0).getCreatedAt(), delWeeks * 7) && CommonUtil.isPastTime(followTime, delWeeks * 7);
		}
		// ツィートがない場合、
		else {
			// フォロー時間が、○週間超えの場合
			unFollowFlg = CommonUtil.isPastTime(followTime, delWeeks * 7);
		}

		return unFollowFlg;
	}
}