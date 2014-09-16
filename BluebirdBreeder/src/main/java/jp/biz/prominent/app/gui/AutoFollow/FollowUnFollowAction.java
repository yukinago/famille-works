package jp.biz.prominent.app.gui.AutoFollow;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.UserUtil;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;
import twitter4j.TwitterException;

/**
 * アンフォロー処理
 *
 * @author famille
 */
@SuppressWarnings("serial")
@CommonsLog
public class FollowUnFollowAction extends CommonAction {

	private AutoFollowPanel pnlAutoFollow;
	private JTable tblUserList;
	private List<UserInfo> userInfoList;

	public FollowUnFollowAction(MainFrame mainFrame, JTable tblUserList, List<UserInfo> userInfoList) {
		super.mainFrame = mainFrame;
		this.tblUserList = tblUserList;
		this.userInfoList = userInfoList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動フォロー設定】アンフォロー処理。");

		pnlAutoFollow = (AutoFollowPanel) ((JButton) e.getSource()).getParent();

		int ret = CommonUtil.showConfirm("選択対象の再フォローを禁止します。よろしいですか？", false);
		if (ret == JOptionPane.YES_OPTION) {
			super.actionPerformed(e);
		}
	}

	@Override
	protected void doEvent() {
		pnlAutoFollow = mainFrame.bodyPanel.pnlAutoFollow;
		DefaultTableModel tableModel = (DefaultTableModel) pnlAutoFollow.tblFollower.getModel();

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if ((Boolean) tableModel.getValueAt(i, 0) && Constants.FOLLOW_SERVER.equals(tableModel.getValueAt(i, 1).toString())) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "サーバ側設定した件の再フォロー禁止はできません。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		int userIndex = mainFrame.bodyPanel.userIndex;
		boolean existFlg = false;
		List<FollowerEntity> followerEntityList = pnlAutoFollow.userInfo.getUserEntity().getFollowerList();
		FollowerEntity followerEntity = null;

		FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			// 選択行のみ
			if ((Boolean) tableModel.getValueAt(i, 0)) {
				// 画面データ更新
				tableModel.setValueAt(Constants.FOLLOW_FORBI, i, 1);

				try {
					// サーバ側：アンフォロー処理
					userInfoList.get(userIndex).unFollow((Long) tableModel.getValueAt(i, 2), (String) tableModel.getValueAt(i, 3));
				}
				catch (TwitterException e) {
					super.dialog.cancel();
					UserUtil.checkException(e, mainFrame);
					return;
				}

				existFlg = false;
				for (FollowerEntity followerEntityTmp : followerEntityList) {
					if (tableModel.getValueAt(i, 2).equals(followerEntityTmp.getFid())) {
						followerEntityTmp.setStatus(Constants.FOLLOW_FORBI);

						// DB更新
						follower.update(followerEntityTmp);

						existFlg = true;
						break;
					}
				}

				// 存在しない場合
				if (existFlg == false) {
					followerEntity = new FollowerEntity();
					followerEntity.setUid(pnlAutoFollow.userInfo.getUserEntity().getUid());
					followerEntity.setUnfollow(((Boolean) tableModel.getValueAt(i, 0)) ? Constants.FLG_ON : Constants.FLG_OFF);
					followerEntity.setStatus(tableModel.getValueAt(i, 1).toString());
					followerEntity.setFid(Long.valueOf(tableModel.getValueAt(i, 2).toString()));
					followerEntity.setName(tableModel.getValueAt(i, 3).toString());
					followerEntity.setImageUrl(tableModel.getValueAt(i, 4).toString());

					// DB登録
					follower.insert(followerEntity);
				}
			}
		}

		super.dialog.cancel();
		JOptionPane.showMessageDialog(mainFrame, "選択対象の再フォローを禁止しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
