package jp.biz.prominent.app.gui.Header;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.Constants.FollowResult;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.UserUtil;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * 自動フォロータスク
 *
 * @author famille
 */
@CommonsLog
public class AutoFollowTask extends SwingWorker<String, Integer> implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 6968544345050901496L;

	/** リトライ間隔 */
	private static final long RETRY_DELAY = 30 * 1000;

	private static final int RETRY_MAX = 3;

	private MainFrame mainFrame;
	private HeaderPanel pnlHeader;
	private UserInfo userInfo;
	private boolean isRunning = false;

	/**
	 * コンストラクタ
	 *
	 * @param mainFrame メインフレーム
	 * @param pnlHeader ヘッダーパネル
	 * @param userInfo ユーザー情報
	 */
	public AutoFollowTask(MainFrame mainFrame, HeaderPanel pnlHeader, UserInfo userInfo) {
		this.mainFrame = mainFrame;
		this.pnlHeader = pnlHeader;
		this.userInfo = userInfo;
	}

	/**
	 * バッググランド処理（長い処理）
	 */
	@Override
	protected String doInBackground() throws Exception {
		isRunning = true;

		FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);

		UserEntity userEntity = userInfo.getUserEntity();

		// フォロワー（自分をフォローしている）一覧
		List<User> followerUserList = userInfo.getFollowerList(mainFrame);
		// フレンド（自分がフォローしている）一覧
		List<User> friendList = userInfo.getServerFriendsList(mainFrame);

		// フォローすべき対象ユーザー一覧
		List<FollowerEntity> followerEntityList = userEntity.getFollowerList();

		appendAppLog("自動フォロー開始：" + userEntity.getName());
		log.info("【自動フォロー】自動フォロー開始：" + userEntity.getName());

		// ----------------------------------------------
		// 新規フォロー対象のフォロー
		// ----------------------------------------------
		if (!followNewTarget(follower, userEntity, followerUserList, friendList, followerEntityList)) {
			// 処理キャンセル時は終了する
			return null;
		}

		// ----------------------------------------------
		// 自動アンフォロー処理
		// ----------------------------------------------
		if (!autoUnfollow(follower, userEntity, followerUserList, friendList, followerEntityList)) {
			// 処理キャンセル時は終了する
			return null;
		}

		// リフォロー実行が有効の場合
		if (userEntity.checkRefollow()) {

			// 重複可が選択されている場合
			if (userEntity.checkDuplicate()) {
				// ----------------------------------------------
				// 禁止した対象の自動リフォロー処理
				// ----------------------------------------------
				if (!refollowBannedUser(follower, userEntity, followerUserList, friendList, followerEntityList)) {
					// 処理キャンセル時は終了する
					return null;
				}
			}

			// ----------------------------------------------
			// フォロワーの自動フォロー処理
			// ----------------------------------------------
			if (!refollowFollower(follower, userEntity, followerUserList, friendList, followerEntityList)) {
				// 処理キャンセル時は終了する
				return null;
			}
		}

		return null;
	}

	/**
	 * フォロワーのリフォローを実施する。
	 * 
	 * @param follower フォロワー情報へのアクセサ
	 * @param userEntity ユーザーエンティティ
	 * @param followerUserList フォロワーユーザー一覧
	 * @param friendList フレンドユーザー一覧
	 * @param followerEntityList フォロワーエンティティ一覧
	 * @return 処理継続フラグ
	 * @throws InterruptedException スレッドの割り込みが発生した場合
	 */
	private boolean refollowFollower(FollowerAccess follower, UserEntity userEntity, List<User> followerUserList, List<User> friendList, List<FollowerEntity> followerEntityList) throws InterruptedException {

		appendAppLog("フォロワーの自動フォロー開始：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー開始：" + userEntity.getName());

		OUTER: for (User followerUser : followerUserList) {
			// 処理中止の場合
			if (isCancelled()) {
				return false;
			}

			// ローカルの対象リストにあるかチェック
			for (FollowerEntity followerEntityLocal : followerEntityList) {

				// ※重複不可の設定で、かつ対象ユーザーが再フォロー禁止リストに無い場合に限る。
				if (followerUser.getId() == followerEntityLocal.getFid()
						&& !(Constants.FOLLOW_FORBI.equals(followerEntityLocal.getStatus())
						&& !userEntity.checkDuplicate())) {
					continue OUTER;
				}
			}

			// サーバーのフォロー済みであるかチェック
			for (User friend : friendList) {
				if (followerUser.getId() == friend.getId()) {
					continue OUTER;
				}
			}

			// 存在しない場合、フォローを行う
			for (int i = 0; i < RETRY_MAX; i++) {
				try {
					// フォロー中ユーザ
					firePropertyChange("userIcon", "", CommonUtil.getImgTxt2(userEntity.getImageUrl(), 40, 40));
					firePropertyChange("userId", "", userEntity.getUid());

					// サーバ側フォロー
					Constants.FollowResult limitFlg = userInfo.doFollow(
							followerUser.getId(),
							followerUser.getName(),
							userEntity,
							followerUserList.size(),
							friendList.size(),
							true);

					// フォロー数が上限数になっている場合、
					if (limitFlg == Constants.FollowResult.USER_COUNT_OVER) {
						appendAppLog("フォロワーの自動フォロー：フォローできる上限に達しました。" + userEntity.getName());
						log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー：フォローできる上限に達しました。" + userEntity.getName());
						break OUTER;
					}

					appendAppLog("フォロワーの自動フォロー：" + userEntity.getName() + "「" + followerUser.getName() + "」");
					log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー：" + userEntity.getName() + "「" + followerUser.getName() + "」");

					// DBに追加
					FollowerEntity followerNew = new FollowerEntity();

					followerNew.setUid(userEntity.getUid());
					followerNew.setStatus(Constants.FOLLOW_LOCAL);
					followerNew.setFid(followerUser.getId());
					followerNew.setName(followerUser.getName());
					followerNew.setImageUrl(CommonUtil.getUrlImgTxt(followerUser.getProfileImageURL().toString(), 35, 35));
					followerNew.setUnfollow(Constants.FLG_OFF);
					followerNew.setFollowTime(CommonUtil.getSystemTime());

					follower.insert(followerNew);

					// 「twitter APIの制限により、○分休止します。」
					if (limitFlg == FollowResult.TWITTER_API_LIMIT) {
						appendAppLog("フォロワーの自動フォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
						log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
						Thread.sleep(15 * 60 * 1000);
					}

					// フォロー間隔:180秒＋１～60秒のゆらぎ
					Thread.sleep(Constants.FOLLOW_SLEEP * 1000 + (new Random().nextInt(Constants.FOLLOW_RANDOM) + 1) * 1000);
					break;
				}
				catch (TwitterException e) {
					log.error(e);
					String check = UserUtil.checkException(e);
					if (check != null) {
						appendAppLog("リフォロー_フォロワーの自動フォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerUser.getName() + "」");
						break;
					}
					else {
						// 間隔後リトライ:30秒
						Thread.sleep(RETRY_DELAY);
						if (i == RETRY_MAX - 1) {
							check = e.getStatusCode() + ":予想外エラーです。";
							break;
						}
					}
					log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerUser.getName() + "」");
				}
				catch (Exception e) {
					log.error(e);
					break;
				}
			}
		}

		appendAppLog("フォロワーの自動フォロー完了：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_フォロワーの自動フォロー完了：" + userEntity.getName());

		return true;
	}

	/**
	 * 禁止ユーザーのリフォローを実施する。
	 * 
	 * @param follower フォロワー情報へのアクセサ
	 * @param userEntity ユーザーエンティティ
	 * @param followerUserList フォロワーユーザー一覧
	 * @param friendList フレンドユーザー一覧
	 * @param followerEntityList フォロワーエンティティ一覧
	 * @return 処理継続フラグ
	 * @throws InterruptedException スレッドの割り込みが発生した場合
	 */
	private boolean refollowBannedUser(FollowerAccess follower, UserEntity userEntity, List<User> followerUserList, List<User> friendList, List<FollowerEntity> followerEntityList) throws InterruptedException {

		appendAppLog("禁止対象の自動リフォロー開始：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー開始：" + userEntity.getName());

		OUTER: for (FollowerEntity followerEntity : followerEntityList) {
			// 処理中止の場合
			if (isCancelled()) {
				return false;
			}

			// 禁止リストの対象
			if (Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
				for (int i = 0; i < RETRY_MAX; i++) {
					try {
						// フォロー中ユーザ
						firePropertyChange("userIcon", "", CommonUtil.getImgTxt2(userEntity.getImageUrl(), 40, 40));
						firePropertyChange("userId", "", userEntity.getUid());

						// サーバ側フォロー
						FollowResult limitFlg = userInfo.doFollow(
								followerEntity.getFid(),
								followerEntity.getName(),
								userEntity,
								followerUserList.size(),
								friendList.size(),
								true);

						// フォロー数が上限数になっている場合、
						if (limitFlg == FollowResult.USER_COUNT_OVER) {
							appendAppLog("禁止対象の自動リフォロー：フォローできる上限に達しました。" + userEntity.getName());
							log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー：フォローできる上限に達しました。" + userEntity.getName());
							break OUTER;
						}

						appendAppLog("禁止対象の自動リフォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
						log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");

						// Localフォロー済
						followerEntity.setStatus(Constants.FOLLOW_LOCAL);
						followerEntity.setFollowTime(CommonUtil.getSystemTime());

						// DB更新
						follower.update(followerEntity);

						// 「twitter APIの制限により、○分休止します。」
						if (limitFlg == FollowResult.TWITTER_API_LIMIT) {
							appendAppLog("禁止対象の自動リフォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
							log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
							Thread.sleep(15 * 60 * 1000);
						}

						// フォロー間隔:180秒＋１～60秒のゆらぎ
						Thread.sleep(Constants.FOLLOW_SLEEP * 1000 + (new Random().nextInt(Constants.FOLLOW_RANDOM) + 1) * 1000);
						break;
					}
					catch (TwitterException e) {
						log.error(e);
						String check = UserUtil.checkException(e);
						if (check != null) {
							appendAppLog("リフォロー_禁止対象の自動リフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
							break;
						}
						else {
							// 間隔後リトライ:30秒
							Thread.sleep(RETRY_DELAY);
							if (i == RETRY_MAX - 1) {
								check = e.getStatusCode() + ":予想外エラーです。";
								break;
							}
						}
						log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
					}
					catch (Exception e) {
						log.error(e);
						break;
					}
				}
			}
		}

		appendAppLog("禁止対象の自動リフォロー完了：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_禁止対象の自動リフォロー完了：" + userEntity.getName());

		return true;
	}

	/**
	 * 自動アンフォローを実施する。
	 * 
	 * @param follower フォロワー情報へのアクセサ
	 * @param userEntity ユーザーエンティティ
	 * @param followerUserList フォロワーユーザー一覧
	 * @param friendList フレンドユーザー一覧
	 * @param followerEntityList フォロワーエンティティ一覧
	 * @return 処理継続フラグ
	 * @throws InterruptedException スレッドの割り込みが発生した場合
	 */
	private boolean autoUnfollow(FollowerAccess follower, UserEntity userEntity, List<User> followerUserList, List<User> friendList, List<FollowerEntity> followerEntityList) throws InterruptedException {
		appendAppLog("自動アンフォロー開始：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_自動アンフォロー開始：" + userEntity.getName());

		boolean unFollowFlg = false;

		for (FollowerEntity followerEntity : followerEntityList) {
			// 処理中止の場合
			if (isCancelled()) {
				return false;
			}

			// ツールでフォローした対象
			if (Constants.FOLLOW_LOCAL.equals(followerEntity.getStatus())) {
				unFollowFlg = false;

				// 自動アンフォロー判定
				for (int i = 0; i < RETRY_MAX; i++) {
					try {
						// ○日以内にフォロワーとならなかったユーザ
						// ○週間以上ツィートの無いユーザー
						unFollowFlg = userInfo.isUnFollow(followerEntity.getFid(), followerEntity.getFollowTime(), userEntity.getThresholdNoFollow(), userEntity.getThresholdNoTweet());

						String strIsFollow = null;

						if (unFollowFlg) {
							strIsFollow = "アンフォロー対象";
						}
						else {
							strIsFollow = "アンフォロー対象外";
						}

						appendAppLog("自動アンフォロー判定：" + userEntity.getName() + "「" + followerEntity.getName() + "」" + strIsFollow);
						log.info("【自動フォロー】リフォロー_自動アンフォロー判定：" + userEntity.getName() + "「" + followerEntity.getName() + "」" + strIsFollow);

						break;
					}
					catch (TwitterException e) {
						log.error(e);
						String check = UserUtil.checkException(e);
						if (check != null) {
							appendAppLog("リフォロー_自動アンフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
							break;
						}
						else {
							// 間隔後リトライ:30秒
							Thread.sleep(RETRY_DELAY);
							if (i == RETRY_MAX - 1) {
								check = e.getStatusCode() + ":予想外エラーです。";
							}
						}
						log.info("【自動フォロー】リフォロー_自動アンフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
					}
				}

				// アンフォロー対象の場合
				if (unFollowFlg) {
					for (int i = 0; i < RETRY_MAX; i++) {
						try {
							// フォロー中ユーザ
							firePropertyChange("userIcon", "", CommonUtil.getImgTxt2(userEntity.getImageUrl(), 40, 40));
							firePropertyChange("userId", "", userEntity.getUid());

							// サーバ側アンフォロー
							userInfo.unFollow(followerEntity.getFid(), followerEntity.getName());
							appendAppLog("自動アンフォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
							log.info("【自動フォロー】リフォロー_自動アンフォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");

							// 禁止リストに保存
							followerEntity.setStatus(Constants.FOLLOW_FORBI);

							// DB更新
							follower.update(followerEntity);

							// アンフォロー処理間隔:180秒＋１～60秒のゆらぎ
							Thread.sleep(Constants.FOLLOW_SLEEP * 1000 + (new Random().nextInt(Constants.FOLLOW_RANDOM) + 1) * 1000);
							break;
						}
						catch (TwitterException e) {
							log.error(e);
							String check = UserUtil.checkException(e);
							if (check != null) {
								appendAppLog("リフォロー_自動アンフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
								break;
							}
							else {
								// 間隔後リトライ:30秒
								Thread.sleep(RETRY_DELAY);
								if (i == RETRY_MAX - 1) {
									check = e.getStatusCode() + ":予想外エラーです。";
									break;
								}
							}
							log.info("【自動フォロー】リフォロー_自動アンフォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
						}
					}
				}
				// アンフォロー対象ではない場合
				else {
					// 自動アンフォロー判定間隔:60秒
					Thread.sleep(60 * 1000);
				}
			}
		}

		appendAppLog("自動アンフォロー完了：" + userEntity.getName());
		log.info("【自動フォロー】リフォロー_自動アンフォロー完了：" + userEntity.getName());

		return true;
	}

	/**
	 * 新規フォローを実施する。
	 * 
	 * @param follower フォロワー情報へのアクセサ
	 * @param userEntity ユーザーエンティティ
	 * @param followerUserList フォロワーユーザー一覧
	 * @param friendList フレンドユーザー一覧
	 * @param followerEntityList フォロワーエンティティ一覧
	 * @return 処理継続フラグ
	 * @throws InterruptedException スレッドの割り込みが発生した場合
	 */
	private boolean followNewTarget(FollowerAccess follower, UserEntity userEntity, List<User> followerUserList, List<User> friendList, List<FollowerEntity> followerEntityList) throws InterruptedException {

		if (userEntity.checkFollow()) {
			appendAppLog("新規フォロー開始：" + userEntity.getName());
			log.info("【自動フォロー】新規フォロー開始：" + userEntity.getName());

			// フォロー対象件数分
			OUTER: for (FollowerEntity followerEntity : followerEntityList) {
				// 処理中止の場合
				if (isCancelled()) {
					return false;
				}

				// 未フォローの対象
				if (Constants.FOLLOW_NOT.equals(followerEntity.getStatus())) {
					for (int i = 0; i < RETRY_MAX; i++) {
						try {
							// フォロー中ユーザ
							firePropertyChange("userIcon", "", CommonUtil.getImgTxt2(userEntity.getImageUrl(), 40, 40));
							firePropertyChange("userId", "", userEntity.getUid());
							// サーバ側フォロー
							FollowResult limitFlg = userInfo.doFollow(followerEntity.getFid(), followerEntity.getName(), userEntity, followerUserList.size(), friendList.size(), false);

							// フォロー数が上限数になっている場合、
							if (limitFlg == FollowResult.USER_COUNT_OVER || limitFlg == FollowResult.DAY_COUNT_OVER) {
								appendAppLog("新規フォロー：フォローできる上限に達しました。" + userEntity.getName());
								log.info("【自動フォロー】新規フォロー：フォローできる上限に達しました。" + userEntity.getName());
								break OUTER;
							}

							appendAppLog("新規フォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
							log.info("【自動フォロー】新規フォロー：" + userEntity.getName() + "「" + followerEntity.getName() + "」");

							// Localフォロー済
							followerEntity.setStatus(Constants.FOLLOW_LOCAL);
							followerEntity.setFollowTime(CommonUtil.getSystemTime());

							// DB更新
							follower.update(followerEntity);

							// 「twitter APIの制限により、○分休止します。」
							if (limitFlg == FollowResult.TWITTER_API_LIMIT) {
								firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 新規フォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
								log.info("【自動フォロー】新規フォロー：Twitter APIの制限により、15分休止します。" + userEntity.getName());
								Thread.sleep(15 * 60 * 1000);
							}

							// フォロー間隔:180秒＋１～60秒のゆらぎ
							Thread.sleep(Constants.FOLLOW_SLEEP * 1000 + (new Random().nextInt(Constants.FOLLOW_RANDOM) + 1) * 1000);
							break;
						}
						catch (TwitterException e) {
							log.error(e);
							String check = UserUtil.checkException(e);
							if (check != null) {
								appendAppLog("新規フォロー失敗(" + check + "):" + userEntity.getName() + "「" + followerEntity.getName() + "」");
								break;
							}
							else {
								Thread.sleep(RETRY_DELAY);
								if (i == RETRY_MAX - 1) {
									check = e.getStatusCode() + ":予想外エラーです。";
									break;
								}
							}
							log.info("【自動フォロー】新規フォロー失敗(" + check + ")：" + userEntity.getName() + "「" + followerEntity.getName() + "」");
						}
						catch (Exception e) {
							log.error(e);
							break;
						}
					}
				}
			}
			appendAppLog("新規フォロー完了：" + userEntity.getName());
			log.info("【自動フォロー】新規フォロー完了：" + userEntity.getName());
		}

		return true;
	}

	/**
	 * 処理終了GUI更新
	 */
	@Override
	protected void done() {
		isRunning = false;

		// キャンセルの場合
		if (isCancelled()) {
			appendAppLog("自動フォロー中止：" + userInfo.getUserEntity().getName());
			log.info("【自動フォロー】自動フォロー中止：" + userInfo.getUserEntity().getName());
		}
		// 終了の場合
		else {
			appendAppLog("自動フォロー完了：" + userInfo.getUserEntity().getName());
			log.info("【自動フォロー】自動フォロー完了：" + userInfo.getUserEntity().getName());
		}

		// ユーザ数分Task
		List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
		AutoFollowTask followTask;
		boolean hasRunUser = false;
		for (int i = 0; i < userInfoList.size(); i++) {
			// 実行中ユーザがある場合
			followTask = userInfoList.get(i).getFollowTask();
			if (followTask != null && followTask.isRunning) {
				hasRunUser = true;
				break;
			}
		}

		// 実行中ユーザがない場合
		if (hasRunUser == false) {
			ImageIcon followIcon1 = new ImageIcon("icon/AutoFollowStart1.gif");
			ImageIcon followIcon2 = new ImageIcon("icon/AutoFollowStart2.gif");
			pnlHeader.btnAutoFollow.setIcon(followIcon1);
			pnlHeader.btnAutoFollow.setRolloverIcon(followIcon2);
			pnlHeader.runAutoFollow = false;
		}
	}

	/**
	 * アプリケーションへログを出力する。
	 * 
	 * @param message 出力メッセージ
	 */
	private void appendAppLog(String message) {
		firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " " + message);
	}
}
