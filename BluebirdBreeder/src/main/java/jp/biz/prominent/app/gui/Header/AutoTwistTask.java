package jp.biz.prominent.app.gui.Header;

import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.TweetEntity;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;
import twitter4j.TwitterException;

/**
 * 自動ツィートタスク
 *
 * @author famille
 */
@CommonsLog
public class AutoTwistTask extends SwingWorker<String, Integer> {

	private MainFrame mainFrame;
	private HeaderPanel pnlHeader;
	private UserInfo userInfo;
	public boolean isRunning = false;

	public AutoTwistTask(MainFrame mainFrame, HeaderPanel pnlHeader, UserInfo userInfo) {
		this.mainFrame = mainFrame;
		this.pnlHeader = pnlHeader;
		this.userInfo = userInfo;
	}

	/**
	 * 長い処理
	 */
	@Override
	protected String doInBackground() throws Exception {
		isRunning = true;

		UserEntity userEntity = userInfo.getUserEntity();

		firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート開始：" + userEntity.getName());
		log.info("【自動ツイート】自動ツィート開始：" + userEntity.getName());

		TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

		// ツイート内容分
		for (TweetEntity tweetEntity : userEntity.getTweetList()) {
			// 処理取消の場合
			if (isCancelled()) {
				return null;
			}

			// ツィート対象の場合
			if (tweetEntity.checkObjflg()) {
				try {
					// 自動ツィート
					userInfo.doTweet(tweetEntity.getTemplate());
					firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");
					log.info("【自動ツイート】自動ツィート:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");

					// ツィート済
					tweetEntity.setSelected(Constants.FLG_OFF);

					// DB保存
					tweet.update(tweetEntity);
				}
				catch (TwitterException e) {
					if (e.getStatusCode() == 403) {
						log.error(e);
						firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート制限:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");
						log.info("【自動ツイート】自動ツィート制限:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");
					}
					else {
						log.error(e);
						firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート失敗:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");
						log.info("【自動ツイート】自動ツィート失敗:" + userEntity.getName() + "「" + tweetEntity.getTemplate() + "」");
					}
				}

				try {
					// ツィート間隔 + ゆらぎ
					Thread.sleep(userEntity.getInterval() * 60 * 1000 + new Random().nextInt(userEntity.getRange()) * 1000);
				}
				catch (Exception e) {
					// None
				}
			}
		}

		return null;
	}

	/**
	 * 処理終了GUI更新
	 */
	@Override
	protected void done() {
		isRunning = false;

		// 画面表示中のツィート情報更新
		if (userInfo.getId().equals(mainFrame.bodyPanel.userInfo.getId())) {
			mainFrame.bodyPanel.pnlAutoTwist.setUserData(userInfo);
		}

		// キャンセルの場合
		if (isCancelled()) {
			firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート中止：" + userInfo.getUserEntity().getName());
			log.info("【自動ツィート】自動ツィート中止");
		}
		// 終了の場合
		else {
			firePropertyChange("logWrite", "", CommonUtil.getSystemTime() + " 自動ツィート完了：" + userInfo.getUserEntity().getName());
			log.info("【自動ツィート】自動ツィート完了：" + userInfo.getUserEntity().getName());
		}

		// ユーザ数分Task
		List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
		AutoTwistTask twistTask;
		boolean hasRunUser = false;
		for (int i = 0; i < userInfoList.size(); i++) {
			// 実行中ユーザがある場合
			twistTask = userInfoList.get(i).getTwistTask();
			if (twistTask != null && twistTask.isRunning) {
				hasRunUser = true;
				break;
			}
		}

		// 実行中ユーザがない場合
		if (hasRunUser == false) {
			ImageIcon twistIcon1 = new ImageIcon("icon/AutoTwistStart1.gif");
			ImageIcon twistIcon2 = new ImageIcon("icon/AutoTwistStart2.gif");
			pnlHeader.btnAutoTwitter.setIcon(twistIcon1);
			pnlHeader.btnAutoTwitter.setRolloverIcon(twistIcon2);
			pnlHeader.runAutoTwist = false;
		}
	}
}
