package jp.biz.prominent.app.gui.IdModify;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class ModifySaveAction extends CommonAction {

	private JTable tblUserList;
	private List<UserInfo> userInfoList;
	private IdModifyPanel pnlIdModify;

	public ModifySaveAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void doEvent() {
		log.info("【ユーザ修正】保存処理。");

		this.saveOkFlg = false;
		pnlIdModify = mainFrame.bodyPanel.pnlIdModify;

		// 入力文字数チェック
		if (pnlIdModify.txtName.getText().length() > 20) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "ニックネームは20文字以内にしてください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// ファイルチェック
		if (!"".equals(pnlIdModify.txtIcon.getText())) {
			File imgFile = new File(pnlIdModify.txtIcon.getText());

			// ファイル存在チェック
			if (imgFile.exists() == false || imgFile.isFile() == false) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "指定した画像ファイルが存在しません。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// ファイル拡張子チェック
			if (pnlIdModify.txtIcon.getText().toLowerCase().endsWith(".gif") == false && pnlIdModify.txtIcon.getText().toLowerCase().endsWith(".jpg") == false && pnlIdModify.txtIcon.getText().toLowerCase().endsWith(".jpeg") == false && pnlIdModify.txtIcon.getText().toLowerCase().endsWith(".png") == false) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "正しい画像ファイル(*.gif,*.jpg,*.jpeg,*.png)を指定してください。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// ファイルサイズチェック
			if (imgFile.length() > 700 * 1024) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "画像ファイルは700KB以下にしてください。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// データ保存
		pnlIdModify.saveData();

		// DB更新
		int rowIndex = mainFrame.bodyPanel.userIndex;
		userInfoList.set(rowIndex, pnlIdModify.userInfo);

		UserAccess user = InjectionUtils.getInstance(UserAccess.class);
		user.update(pnlIdModify.userInfo.getUserEntity());

		// GUIユーザリストの更新
		DefaultTableModel tableModel = (DefaultTableModel) tblUserList.getModel();
		tableModel.setValueAt(pnlIdModify.userInfo.getUserEntity().getName(), rowIndex, 1);
		tableModel.setValueAt(CommonUtil.getImgTxt2(pnlIdModify.userInfo.getUserEntity().getImageUrl(), 35, 35), rowIndex, 2);

		// データ保持
		pnlIdModify.backupData();

		if (selfFlg) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
		}
		this.saveOkFlg = true;
	}

}
