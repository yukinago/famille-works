package jp.biz.prominent.app.gui.IdModify;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

/**
 * ユーザー削除ボタン
 *
 * @author famille
 */
@SuppressWarnings("serial")
@CommonsLog
public class ModifyDeleteAction extends CommonAction {

	private JTable tblUserList;
	private List<UserInfo> userInfoList;
	private IdModifyPanel pnlIdModify;

	public ModifyDeleteAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【ユーザ修正】削除処理。");

		pnlIdModify = (IdModifyPanel) ((JButton) e.getSource()).getParent();
		UserEntity userEntity = pnlIdModify.userInfo.getUserEntity();
		int ret = CommonUtil.showConfirm(userEntity.getUid() + "：" + userEntity.getName() + "を削除します。よろしいですか？", false);
		// YESの場合、再初期化
		if (ret == JOptionPane.YES_OPTION) {
			super.actionPerformed(e);
		}
	}

	@Override
	protected void doEvent() {
		pnlIdModify = mainFrame.bodyPanel.pnlIdModify;

		int index = mainFrame.bodyPanel.userIndex;

		UserAccess user = InjectionUtils.getInstance(UserAccess.class);
		FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);
		TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

		// DB削除
		user.deleteByUid(pnlIdModify.userInfo.getUserEntity().getUid());
		follower.deleteByUid(pnlIdModify.userInfo.getUserEntity().getUid());
		tweet.deleteByUid(pnlIdModify.userInfo.getUserEntity().getUid());

		userInfoList.remove(index);

		// GUI削除
		DefaultTableModel tableModel = (DefaultTableModel) tblUserList.getModel();
		mainFrame.bodyPanel.innerRowChange = true;
		tableModel.removeRow(index);

		// 1行目を選択する
		if (tableModel.getRowCount() > 0) {
			mainFrame.bodyPanel.userInfo = null;
			tblUserList.changeSelection(0, 0, false, false);
		}
		else {
			mainFrame.headPanel.btnAutoFollow.setEnabled(false);
			mainFrame.headPanel.btnAutoTwitter.setEnabled(false);
			mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(1, false);
			mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(2, false);
			mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(3, false);
		}
		mainFrame.bodyPanel.pnlFuncTab.setSelectedIndex(0);

		super.dialog.cancel();
		JOptionPane.showMessageDialog(mainFrame, "ユーザを削除しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
