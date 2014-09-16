package jp.biz.prominent.app.gui.AutoTwist;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.TweetEntity;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class TwistSaveAction extends CommonAction {

	private JTable tblUserList;
	private List<UserInfo> userInfoList;
	private AutoTwistPanel pnlAutoTwist;

	public TwistSaveAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void doEvent() {
		log.info("【自動ツイート設定】保存処理。");

		this.saveOkFlg = false;
		pnlAutoTwist = mainFrame.bodyPanel.pnlAutoTwist;

		// 入力チェック
		if (CommonUtil.isLong(pnlAutoTwist.txtTime1.getText()) == false || Integer.parseInt(pnlAutoTwist.txtTime1.getText()) < 10 || Integer.parseInt(pnlAutoTwist.txtTime1.getText()) > 3600) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "ツィート間隔に整数(10～3600)を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (CommonUtil.isLong(pnlAutoTwist.txtTime2.getText()) == false || Integer.parseInt(pnlAutoTwist.txtTime2.getText()) < 0 || Integer.parseInt(pnlAutoTwist.txtTime2.getText()) > 180) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "ゆらぎに整数(0～180)を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 画面データ保存
		pnlAutoTwist.saveData();

		// DB更新
		UserEntity userEntity = pnlAutoTwist.userInfo.getUserEntity();
		UserAccess user = InjectionUtils.getInstance(UserAccess.class);
		TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

		user.update(userEntity);

		List<TweetEntity> tweetEntityList = userEntity.getTweetList();
		for (TweetEntity tweetEntity : tweetEntityList) {
			tweet.update(tweetEntity);
		}

		// ツイート情報再設定
		pnlAutoTwist.userInfo.getUserEntity().setTweetList(tweet.selectByUid(pnlAutoTwist.userInfo.getUserEntity().getUid()));

		// データ保持
		int index = mainFrame.bodyPanel.userIndex;
		userInfoList.set(index, pnlAutoTwist.userInfo);
		pnlAutoTwist.backupData();

		pnlAutoTwist.unSaveFlg = false;

		if (selfFlg) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
		}
		this.saveOkFlg = true;

	}
}
