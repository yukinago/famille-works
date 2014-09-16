package jp.biz.prominent.app.gui.AutoTwist;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.TweetEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class TwistDeleteAction extends AbstractAction {

	private MainFrame mainFrame = null;
	private JTable tblUserList;
	private List<UserInfo> userInfoList;

	public TwistDeleteAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		this.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動ツイート設定】選択削除処理。");

		AutoTwistPanel pnlAutoTwist = (AutoTwistPanel) ((JButton) e.getSource()).getParent();

		TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

		int ret = CommonUtil.showConfirm("選択したツイート内容を削除します。よろしいですか？", false);

		if (ret == JOptionPane.YES_OPTION) {
			DefaultTableModel tableModel = (DefaultTableModel) pnlAutoTwist.tblTemp.getModel();
			int delCnt = 0;
			TweetEntity tweetEntity = null;

			// 明細件数分ループ
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				// 選択行削除
				if ((Boolean) tableModel.getValueAt(i, 0)) {
					tableModel.removeRow(i);

					// DB削除
					tweetEntity = new TweetEntity();
					tweetEntity.setUid(pnlAutoTwist.userInfo.getUserEntity().getUid());
					tweetEntity.setTid(i + 1);
					tweet.delete(tweetEntity);

					// 既存番号更新
					tweetEntity = new TweetEntity();
					tweetEntity.setUid(pnlAutoTwist.userInfo.getUserEntity().getUid());
					tweetEntity.setTid(i + 1);
					tweet.updateTweetId(tweetEntity);

					i = i - 1;
					delCnt = delCnt + 1;
				}
			}
			if (delCnt == 0) {
				JOptionPane.showMessageDialog(mainFrame, "削除するツイート内容を選択してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			}
			else {
				pnlAutoTwist.txtTemp.setText("");
				pnlAutoTwist.txtTemp.requestFocus();

				// ツイート情報再設定
				pnlAutoTwist.userInfo.getUserEntity().setTweetList(tweet.selectByUid(pnlAutoTwist.userInfo.getUserEntity().getUid()));

				// データ保持
				int index = mainFrame.bodyPanel.userIndex;
				userInfoList.set(index, pnlAutoTwist.userInfo);
				pnlAutoTwist.backupTemplate();

				JOptionPane.showMessageDialog(mainFrame, delCnt + "行ツイート内容を削除しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}
}
