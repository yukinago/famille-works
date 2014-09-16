package jp.biz.prominent.app.gui.AutoFollow;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import jp.biz.prominent.app.gui.Header.AutoFollowTask;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class FollowSaveAction extends CommonAction {

	private AutoFollowPanel pnlAutoFollow;
	private JTable tblUserList;
	private List<UserInfo> userInfoList;

	public FollowSaveAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void doEvent() {
		log.info("【自動フォロー設定】保存処理。");

		this.saveOkFlg = false;
		pnlAutoFollow = mainFrame.bodyPanel.pnlAutoFollow;

		// 入力チェック
		if (CommonUtil.isLong(pnlAutoFollow.txtDelDays.getText()) == false || Integer.parseInt(pnlAutoFollow.txtDelDays.getText()) < 1) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "日数に正数を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (CommonUtil.isLong(pnlAutoFollow.txtDelWeeks.getText()) == false || Integer.parseInt(pnlAutoFollow.txtDelWeeks.getText()) < 1) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "週数に正数を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (CommonUtil.isLong(pnlAutoFollow.txtFollowMax.getText()) == false || Integer.parseInt(pnlAutoFollow.txtFollowMax.getText()) < 1) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "１日のフォロー数に正数を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// １日のフォロー数に250件を超えている場合
		if (Integer.parseInt(pnlAutoFollow.txtFollowMax.getText()) > 250) {
			int ret = CommonUtil.showConfirm3("短時間に大量のフォローを行うとアカウントを凍結される場合があります。", false);
			// ［取消］クリックで入力値をクリア
			if (ret == JOptionPane.NO_OPTION) {
				pnlAutoFollow.txtFollowMax.setText("");
				return;
			}
		}

		int userIndex = mainFrame.bodyPanel.userIndex;

		// 自動フォロー中の場合
		if (mainFrame.headPanel.runAutoFollow) {
			int ret = CommonUtil.showConfirm("設定を変更すると自動フォローの結果に反映されます。よろしいですか？", false);
			// 「いいえ」で入力画面に戻る
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}

			// 操作中のユーザに対して、フォロータスク再起動
			AutoFollowTask followTask = userInfoList.get(userIndex).getFollowTask();
			followTask.cancel(true);
			followTask = new AutoFollowTask(mainFrame, mainFrame.headPanel, userInfoList.get(userIndex));
			followTask.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					JTable tblLogList = mainFrame.bodyPanel.tblLogList;
					DefaultTableModel logTableModel = (DefaultTableModel) tblLogList.getModel();
					if ("logWrite".equals(evt.getPropertyName())) {
						logTableModel.addRow(new Object[] { (String) evt.getNewValue() });
						Rectangle rect = tblLogList.getCellRect(logTableModel.getRowCount() - 1, 0, true);
						tblLogList.scrollRectToVisible(rect);
						mainFrame.bodyPanel.saveGuiLog((String) evt.getNewValue());
					}
					if ("userIcon".equals(evt.getPropertyName())) {
						mainFrame.headPanel.runIcon.setText((String) evt.getNewValue());
					}
					if ("userId".equals(evt.getPropertyName())) {
						mainFrame.headPanel.runId.setText((String) evt.getNewValue());
					}
				}
			});
			followTask.execute();
		}

		// データ保存
		pnlAutoFollow.saveData();

		// DB更新
		UserEntity userEntity = pnlAutoFollow.userInfo.getUserEntity();

		UserAccess user = InjectionUtils.getInstance(UserAccess.class);
		FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);

		// ユーザー基本情報更新
		user.update(userEntity);
		// 既存の未Follow対象を削除する
		follower.deleteExistUnFollow(userEntity.getUid());

		// 新検索の未Follow対象を保存する
		for (FollowerEntity followEntity : userEntity.getFollowerList()) {
			if (Constants.FOLLOW_NOT.equals(followEntity.getStatus())) {
				follower.insert(followEntity);
			}
		}

		// フォローワーリストを取得する
		List<FollowerEntity> followerList = follower.selectByUid(pnlAutoFollow.userInfo.getUserEntity().getUid());

		// ツイート情報再設定
		pnlAutoFollow.userInfo.getUserEntity().setFollowerList(followerList);

		// データ保持
		userInfoList.set(userIndex, pnlAutoFollow.userInfo);
		pnlAutoFollow.backupData();
		pnlAutoFollow.unSaveFlg = false;

		if (selfFlg) {
			super.dialog.cancel();
			JOptionPane.showMessageDialog(mainFrame, "設定内容を保存しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
		}
		this.saveOkFlg = true;

	}
}
