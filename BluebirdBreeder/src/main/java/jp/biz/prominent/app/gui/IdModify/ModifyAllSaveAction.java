package jp.biz.prominent.app.gui.IdModify;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.gui.MainFrame;
import jp.biz.prominent.app.gui.AutoFollow.FollowSaveAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistSaveAction;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class ModifyAllSaveAction extends CommonAction {

	private JTable tblUserList;
	private List<UserInfo> userInfoList;

	public ModifyAllSaveAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	protected void doEvent() {
		log.info("【ユーザ修正】全保存処理。");

		// ユーザ修正画面
		if (mainFrame.bodyPanel.pnlIdModify.hasUnSaved()) {
			ModifySaveAction action1 = new ModifySaveAction(mainFrame, tblUserList, userInfoList);
			action1.dialog = super.dialog;
			action1.selfFlg = false;
			action1.doEvent();
			if (action1.saveOkFlg == false) {
				return;
			}
		}

		// 自動フォロー設定画面
		if (mainFrame.bodyPanel.pnlAutoFollow.hasUnSaved()) {
			mainFrame.bodyPanel.pnlFuncTab.setSelectedIndex(2);
			FollowSaveAction action2 = new FollowSaveAction(mainFrame, tblUserList, userInfoList);
			action2.dialog = super.dialog;
			action2.selfFlg = false;
			action2.doEvent();
			if (action2.saveOkFlg == false) {
				return;
			}
		}

		// 自動ツィート設定画面
		if (mainFrame.bodyPanel.pnlAutoTwist.hasUnSaved()) {
			mainFrame.bodyPanel.pnlFuncTab.setSelectedIndex(3);
			TwistSaveAction action3 = new TwistSaveAction(mainFrame, tblUserList, userInfoList);
			action3.dialog = super.dialog;
			action3.selfFlg = false;
			action3.doEvent();
			if (action3.saveOkFlg == false) {
				return;
			}
		}

		// ユーザ修正画面に戻る
		mainFrame.bodyPanel.pnlFuncTab.setSelectedIndex(1);
		JOptionPane.showMessageDialog(mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
