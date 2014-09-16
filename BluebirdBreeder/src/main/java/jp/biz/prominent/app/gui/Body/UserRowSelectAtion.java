package jp.biz.prominent.app.gui.Body;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.UserUtil;
import jp.biz.prominent.app.com.gui.ProgressDialog;
import jp.biz.prominent.app.gui.AutoFollow.FollowSaveAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistSaveAction;
import jp.biz.prominent.app.gui.IdModify.ModifySaveAction;
import jp.biz.prominent.app.gui.IdRegist.RegistSaveAction;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class UserRowSelectAtion implements PropertyChangeListener {

	private BodyPanel bodyPanel;
	private ProgressDialog<String> dialog;
	private boolean errFlg;
	protected int newRowIdx;

	public UserRowSelectAtion(BodyPanel bodyPanel) {
		this.bodyPanel = bodyPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		newRowIdx = (Integer) evt.getNewValue();

		// 処理中
		dialog = new ProgressDialog<String>((Frame) SwingUtilities.getAncestorOfClass(Frame.class, bodyPanel.mainFrame), true);
		dialog.setLocationRelativeTo(bodyPanel.mainFrame);
		dialog.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				try {
					doEvent();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return "success";
			}
		});
		dialog.setVisible(true);
	}

	/**
	 * イベント処理
	 */
	protected void doEvent() {
		// ユーザ変更の場合
		if (bodyPanel.userInfo != null && bodyPanel.userInfo.getId().equals(bodyPanel.userInfoList.get(newRowIdx).getId()) == false) {

			// 変更有無チェック
			boolean hasChange = bodyPanel.pnlIdRegist.hasUnSaved();
			hasChange = hasChange || bodyPanel.pnlIdModify.hasUnSaved();
			hasChange = hasChange || bodyPanel.pnlAutoFollow.hasUnSaved();
			hasChange = hasChange || bodyPanel.pnlAutoTwist.hasUnSaved();

			// 変更ありの場合
			if (hasChange) {
				// 警告メッセージ表示
				int ret = CommonUtil.showConfirm2(bodyPanel.pnlIdModify.userInfo.getId() + "：" + bodyPanel.pnlIdModify.userInfo.getName() + "の設定が保存されていません。保存してＩＤを変更しますか？", true);

				// はいの場合、旧ユーザ情報の保存処理
				if (ret == JOptionPane.YES_OPTION) {
					errFlg = true;
					dialog = new ProgressDialog<String>((Frame) SwingUtilities.getAncestorOfClass(Frame.class, bodyPanel.mainFrame), true);
					dialog.setLocationRelativeTo(bodyPanel.mainFrame);
					dialog.submit(new Callable<String>() {
						@Override
						public String call() throws Exception {
							int pnlIndex = bodyPanel.pnlFuncTab.getSelectedIndex();
							// ユーザ新規画面
							if (bodyPanel.pnlIdRegist.hasUnSaved()) {
								RegistSaveAction action0 = new RegistSaveAction(bodyPanel.mainFrame, bodyPanel.tblUserList, bodyPanel.userInfoList);
								action0.dialog = dialog;
								action0.selfFlg = false;
								action0.doEvent();
								if (action0.saveOkFlg == false) {
									return "success";
								}
							}
							// ユーザ修正画面
							if (bodyPanel.pnlIdModify.hasUnSaved()) {
								ModifySaveAction action1 = new ModifySaveAction(bodyPanel.mainFrame, bodyPanel.tblUserList, bodyPanel.userInfoList);
								action1.dialog = dialog;
								action1.selfFlg = false;
								action1.doEvent();
								if (action1.saveOkFlg == false) {
									return "success";
								}
							}
							// 自動フォロー設定画面
							if (bodyPanel.pnlAutoFollow.hasUnSaved()) {
								bodyPanel.pnlFuncTab.setSelectedIndex(2);
								FollowSaveAction action2 = new FollowSaveAction(bodyPanel.mainFrame, bodyPanel.tblUserList, bodyPanel.userInfoList);
								action2.dialog = dialog;
								action2.selfFlg = false;
								action2.doEvent();
								if (action2.saveOkFlg == false) {
									return "success";
								}
							}
							// 自動ツィート設定画面
							if (bodyPanel.pnlAutoTwist.hasUnSaved()) {
								bodyPanel.pnlFuncTab.setSelectedIndex(3);
								TwistSaveAction action3 = new TwistSaveAction(bodyPanel.mainFrame, bodyPanel.tblUserList, bodyPanel.userInfoList);
								action3.dialog = dialog;
								action3.selfFlg = false;
								action3.doEvent();
								if (action3.saveOkFlg == false) {
									return "success";
								}
							}
							errFlg = false;
							bodyPanel.pnlFuncTab.setSelectedIndex(pnlIndex);
							JOptionPane.showMessageDialog(bodyPanel.mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
							return "success";
						}
					});
					dialog.setVisible(true);

					// 保存エラーの場合
					if (errFlg) {
						// 旧ユーザの選択まま
						bodyPanel.innerRowChange = true;
						bodyPanel.tblUserList.changeSelection(bodyPanel.userIndex, 0, false, false);
						return;
					}
				}

				// キャンセルの場合、旧ユーザ情報のまま
				if (ret == JOptionPane.CANCEL_OPTION) {
					// 旧ユーザの選択まま
					bodyPanel.innerRowChange = true;
					bodyPanel.tblUserList.changeSelection(bodyPanel.userIndex, 0, false, false);
					return;
				}
			}
		}
		else if (bodyPanel.userInfo != null) {
			bodyPanel.innerRowChange = false;
			return;
		}

		log.info("ユーザリストの行選択。");

		// 新選択ユーザ
		UserInfo userInfo = bodyPanel.userInfoList.get(newRowIdx);
		bodyPanel.userInfo = UserUtil.loadUserInfo(userInfo);
		bodyPanel.userIndex = newRowIdx;

		// ユーザ登録画面
		bodyPanel.pnlIdRegist.resetData();

		// ユーザ修正画面
		bodyPanel.pnlIdModify.setUserData(bodyPanel.userInfo);
		bodyPanel.pnlIdModify.backupData();

		// 自動フォロー画面
		bodyPanel.pnlAutoFollow.setUserData(bodyPanel.userInfo);
		bodyPanel.pnlAutoFollow.backupData();

		// 自動ツィート画面
		bodyPanel.pnlAutoTwist.setUserData(bodyPanel.userInfo);
		bodyPanel.pnlAutoTwist.backupData();

		// 新ユーザの選択
		bodyPanel.innerRowChange = true;
		bodyPanel.tblUserList.changeSelection(newRowIdx, 0, false, false);
	}
}
