package jp.biz.prominent.app.gui.IdRegist;

import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class RegistConfirmAction extends CommonAction {

	private IdRegistPanel pnlIdRegist;

	public RegistConfirmAction(MainFrame mainFrame) {
		super.mainFrame = mainFrame;
	}

	@Override
	protected void doEvent() {
		log.info("【ユーザ登録】確認処理。");

		pnlIdRegist = mainFrame.bodyPanel.pnlIdRegist;

		// 必須チェック
		String errorMsg = "";
		if (CommonUtil.isEmpty(pnlIdRegist.txtConKey.getText())) {
			errorMsg = "「Consumer key」を入力してください。";
		}
		if (CommonUtil.isEmpty(pnlIdRegist.txtConSec.getText())) {
			if (!"".equals(errorMsg)) {
				errorMsg = errorMsg + "\n";
			}
			errorMsg = errorMsg + "「Consumer secret」を入力してください。";
		}
		if (CommonUtil.isEmpty(pnlIdRegist.txtAccTkn.getText())) {
			if (!"".equals(errorMsg)) {
				errorMsg = errorMsg + "\n";
			}
			errorMsg = errorMsg + "「Access token」を入力してください。";
		}
		if (CommonUtil.isEmpty(pnlIdRegist.txtAccTknSec.getText())) {
			if (!"".equals(errorMsg)) {
				errorMsg = errorMsg + "\n";
			}
			errorMsg = errorMsg + "「Access token secret」を入力してください。";
		}
		// チェックエラーの場合
		if ("".equals(errorMsg) == false) {
			JOptionPane.showMessageDialog(mainFrame, errorMsg, "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 認証チェック
		UserInfo userInfo = UserInfo.getInstance();
		UserEntity userEntity = new UserEntity();
		userEntity.setConsumerKey(pnlIdRegist.txtConKey.getText());
		userEntity.setConsumerSecret(pnlIdRegist.txtConSec.getText());
		userEntity.setAccessToken(pnlIdRegist.txtAccTkn.getText());
		userEntity.setAccessTokenSecret(pnlIdRegist.txtAccTknSec.getText());
		userInfo.setUserEntity(userEntity);
		boolean ret = userInfo.verify(mainFrame);

		// 認証できない場合
		if (ret == false) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "ユーザ認証が失敗しました。正しい情報で入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// データ設定
		pnlIdRegist.setData(userInfo);

		// 項目非活性、保存ボタン活性
		pnlIdRegist.txtConKey.setEnabled(false);
		pnlIdRegist.txtConSec.setEnabled(false);
		pnlIdRegist.txtAccTkn.setEnabled(false);
		pnlIdRegist.txtAccTknSec.setEnabled(false);
		pnlIdRegist.btnConfirm.setEnabled(false);
		pnlIdRegist.btnSave.setEnabled(true);

		super.dialog.cancel();
		JOptionPane.showMessageDialog(mainFrame, "ユーザ認証が成功しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}

}
