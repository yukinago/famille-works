package jp.biz.prominent.app.com;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;
import twitter4j.TwitterException;

/**
 * ユーザ情報Util
 *
 * @author famille
 */
@CommonsLog
public class UserUtil {

	/**
	 * DBから全ユーザ情報を読み出す
	 *
	 * @return
	 */
	public static List<UserInfo> getAllUser() {
		log.info("DBから全ユーザ情報を読み出す。");

		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		try {
			UserAccess user = InjectionUtils.getInstance(UserAccess.class);
			FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);
			TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

			// ユーザー基本情報
			List<UserEntity> userEntityList = user.selectAll();

			UserInfo userInfo = null;
			for (UserEntity userEntity : userEntityList) {

				// フォロー情報
				userEntity.setFollowerList(follower.selectByUid(userEntity.getUid()));
				// ツイート情報
				userEntity.setTweetList(tweet.selectByUid(userEntity.getUid()));

				userInfo = new UserInfo();
				userInfo.setUserEntity(userEntity);
				userInfoList.add(userInfo);
			}
		}
		catch (Exception e) {
			log.error("ユーザーリスト検索エラー。", e);
		}

		return userInfoList;
	}

	/**
	 * DBから指定ユーザ情報を読み出す
	 *
	 * @param userInfo ユーザー情報
	 * @return ユーザー情報
	 */
	public static UserInfo loadUserInfo(UserInfo userInfo) {
		log.info("DBから指定ユーザ情報を読み出す。");

		Long uid = userInfo.getUserEntity().getUid();
		try {
			UserAccess user = InjectionUtils.getInstance(UserAccess.class);
			FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);
			TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

			// ユーザー基本情報
			UserEntity userEntity = user.selectByUid(uid);

			// フォロー情報
			userEntity.setFollowerList(follower.selectByUid(uid));
			// ツイート情報
			userEntity.setTweetList(tweet.selectByUid(uid));

			userInfo.setUserEntity(userEntity);
		}
		catch (Exception e) {
			log.error("指定ユーザー検索エラー。", e);
		}

		return userInfo;
	}

	/**
	 * TwitterExceptionの分析
	 *
	 * @param e
	 * @param mainFrame
	 */
	public static void checkException(TwitterException e, MainFrame mainFrame) {
		log.info("TwitterException", e);

		// 400エラー
		if (e.getStatusCode() == 400) {
			// The request was invalid. An accompanying error message will
			// explain why. This is the status code will be returned during rate
			// limiting.
			// error - Rate limit exceeded. Clients may not make more than 350
			// requests per hour.
			JOptionPane.showMessageDialog(mainFrame, "Twitterのアクセス数（1時間350回）の限界を超えました。しばらく経ってから、再度お試しください。", "エラー", JOptionPane.ERROR_MESSAGE);
		}
		// 403エラー
		else if (e.getStatusCode() == 403) {
			// ・フォロー済みのユーザーを再フォローしようとるするとHTTPコード403を返す
			// ・フォロー制限を超えてフォローをした場合HTTPコード403を返す
			// Status is a duplicate
			JOptionPane.showMessageDialog(mainFrame, "Twitter制限を超えています。", "エラー", JOptionPane.ERROR_MESSAGE);
		}
		// 429エラー
		else if (e.getStatusCode() == 429) {
			// Rate limit exceeded.
			JOptionPane.showMessageDialog(mainFrame, "Twitterの制限を超えています。しばらく経ってから、再度お試しください。", "エラー", JOptionPane.ERROR_MESSAGE);
		}
		// 500エラー
		else if (e.getStatusCode() == 500) {
			// Something is broken. Please post to the group so the Twitter team
			// can investigate.
			JOptionPane.showMessageDialog(mainFrame, "Twitterの技術的な不具合が発生しています。", "エラー", JOptionPane.ERROR_MESSAGE);
		}
		// 502エラー
		else if (e.getStatusCode() == 502) {
			// Twitter is down or being upgraded.
			JOptionPane.showMessageDialog(mainFrame, "Twitterの処理能力の限界を超えました。しばらく経ってから、再度お試しください。", "エラー", JOptionPane.ERROR_MESSAGE);
		}
		else {
			log.error(e);
			JOptionPane.showMessageDialog(mainFrame, "予想外エラー：" + e.getStatusCode(), "エラー", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * TwitterExceptionの分析
	 *
	 * @param e
	 * @return エラーメッセージ
	 */
	public static String checkException(TwitterException e) {
		String returnStr = null;

		// Twitter制限
		if (e.exceededRateLimitation()) {
			returnStr = e.getStatusCode() + ":Twitter制限を超えています。" + e.isErrorMessageAvailable();
		}

		// ネットワークの問題
		if (e.isCausedByNetworkIssue()) {
			returnStr = e.getStatusCode() + ":ネットワークの問題です。" + e.isErrorMessageAvailable();
		}

		// Twitter側エラーメッセージがある場合
		if (e.isErrorMessageAvailable()) {
			returnStr = e.getStatusCode() + ":" + e.getErrorMessage();
		}

		return returnStr;
	}

}