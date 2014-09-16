package jp.biz.prominent.app.gui.IdRegist;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.ToolInformation;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

/**
 * 登録ボタン処理
 *
 * @author famille
 */
@SuppressWarnings("serial")
@CommonsLog
public class RegistSaveAction extends CommonAction {

	private JTable tblUserList;
	private List<UserInfo> userInfoList;
	private IdRegistPanel pnlIdRegist;

	public RegistSaveAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void doEvent() {
		log.info("【ユーザ登録】保存処理。");

		this.saveOkFlg = false;
		pnlIdRegist = mainFrame.bodyPanel.pnlIdRegist;

		// IDリスト存在チェック
		for (int i = 0; i < tblUserList.getRowCount(); i++) {
			// 存在する場合
			if (pnlIdRegist.txtId.getText().equals(tblUserList.getModel().getValueAt(i, 1))) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "同一ツィッターIDが既に登録されています。", "警告", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		// 最大10件まで登録
		if (tblUserList.getRowCount() == ToolInformation.getMaxCount()) {
			super.dialog.cancel();
			if (ToolInformation.isNoLimit()) {
				int ret = CommonUtil.showConfirm(ToolInformation.getMaxCount() + "件以上登録することができますが、動作が保証されていません。登録しますか？", false);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
			}
			else {
				JOptionPane.showMessageDialog(mainFrame, ToolInformation.getMaxCount() + "件以上登録することができません。", "警告", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		// DB登録
		userInfoList.add(0, pnlIdRegist.userInfo);

		UserAccess user = InjectionUtils.getInstance(UserAccess.class);
		user.insert(pnlIdRegist.userInfo.getUserEntity());

		// サーバ処理の3秒待ち
		try {
			Thread.sleep(3000);
		}
		catch (Exception e) {
			//
		}

		// GUIに追加
		DefaultTableModel tableModel = (DefaultTableModel) tblUserList.getModel();
		mainFrame.bodyPanel.innerRowChange = true;
		tableModel.insertRow(0, new Object[] { pnlIdRegist.userInfo.getUserEntity().getUid(), pnlIdRegist.userInfo.getUserEntity().getName(), CommonUtil.getImgTxt2(pnlIdRegist.userInfo.getUserEntity().getImageUrl(), 35, 35) });

		// 入力データクリア
		pnlIdRegist.resetData();

		// 登録ユーザ選択状態
		mainFrame.bodyPanel.tblUserList.firePropertyChange("RowSelect", -1, 0);

		mainFrame.headPanel.btnAutoFollow.setEnabled(true);
		mainFrame.headPanel.btnAutoTwitter.setEnabled(true);
		mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(1, true);
		mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(2, true);
		mainFrame.bodyPanel.pnlFuncTab.setEnabledAt(3, true);

		// 正常登録完了
		if (selfFlg) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
		}
		this.saveOkFlg = true;
	}
}
