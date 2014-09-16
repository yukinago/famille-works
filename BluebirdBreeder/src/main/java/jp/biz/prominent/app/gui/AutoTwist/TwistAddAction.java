package jp.biz.prominent.app.gui.AutoTwist;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.TweetEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class TwistAddAction extends AbstractAction {

	private MainFrame mainFrame = null;
	private JTable tblUserList;
	private List<UserInfo> userInfoList;

	public TwistAddAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		this.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動ツイート設定】新規追加処理。");

		AutoTwistPanel pnlAutoTwist = (AutoTwistPanel) ((JButton) e.getSource()).getParent();

		// 入力チェック
		if ("".equals(pnlAutoTwist.txtTemp.getText())) {
			JOptionPane.showMessageDialog(mainFrame, "ツイート内容を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		DefaultTableModel tableModel = (DefaultTableModel) pnlAutoTwist.tblTemp.getModel();
		String createdTime = CommonUtil.getSystemTime();
		tableModel.addRow(new Object[] { true, createdTime, pnlAutoTwist.txtTemp.getText() });

		// データ設定
		TweetEntity tweetEntity = new TweetEntity();
		tweetEntity.setUid(pnlAutoTwist.userInfo.getUserEntity().getUid());
		tweetEntity.setTid(tableModel.getRowCount());
		tweetEntity.setSelected(Constants.FLG_ON);
		tweetEntity.setCreatedTime(createdTime);
		tweetEntity.setTemplate(pnlAutoTwist.txtTemp.getText());

		// DB登録
		TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);
		tweet.insert(tweetEntity);

		// ツイート情報再設定
		pnlAutoTwist.userInfo.getUserEntity().setTweetList(tweet.selectByUid(pnlAutoTwist.userInfo.getUserEntity().getUid()));

		// データ保持
		int index = mainFrame.bodyPanel.userIndex;
		userInfoList.set(index, pnlAutoTwist.userInfo);
		pnlAutoTwist.txtTemp.setText("");
		pnlAutoTwist.txtTemp.requestFocus();
		pnlAutoTwist.backupTemplate();

		JOptionPane.showMessageDialog(mainFrame, "新規ツイート内容を追加しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
