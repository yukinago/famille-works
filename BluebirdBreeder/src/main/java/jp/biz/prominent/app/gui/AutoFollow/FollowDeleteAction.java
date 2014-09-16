package jp.biz.prominent.app.gui.AutoFollow;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

/**
 * 再フォロー禁止リスト削除
 *
 * @author famille
 */
@SuppressWarnings("serial")
@CommonsLog
public class FollowDeleteAction extends CommonAction {

	private AutoFollowPanel pnlAutoFollow;

	/**
	 * コンストラクタ
	 *
	 * @param mainFrame
	 */
	public FollowDeleteAction(MainFrame mainFrame) {
		super.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動フォロー設定】再フォロー禁止リスト削除処理。");

		pnlAutoFollow = (AutoFollowPanel) ((JButton) e.getSource()).getParent();

		UserEntity userEntity = pnlAutoFollow.userInfo.getUserEntity();
		int ret = CommonUtil.showConfirm(userEntity.getUid() + "：" + userEntity.getName() + "の再フォロー禁止リストを削除します。よろしいですか？", false);

		if (ret == JOptionPane.YES_OPTION) {
			super.actionPerformed(e);
		}
	}

	@Override
	protected void doEvent() {
		pnlAutoFollow = mainFrame.bodyPanel.pnlAutoFollow;

		// GUIのデータ削除
		DefaultTableModel tableModel = (DefaultTableModel) pnlAutoFollow.tblFollower.getModel();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (Constants.FOLLOW_FORBI.equals(tableModel.getValueAt(i, 1).toString())) {
				tableModel.removeRow(i);
				i = i - 1;
			}
		}

		// DB削除
		int count = 0;
		FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);

		List<FollowerEntity> followerEntityList = pnlAutoFollow.userInfo.getUserEntity().getFollowerList();
		for (FollowerEntity followerEntity : followerEntityList) {
			if (Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
				follower.delete(followerEntity);
				count++;
			}
		}

		super.dialog.cancel();
		JOptionPane.showMessageDialog(mainFrame, "再フォロー禁止リスト(" + count + "件)を削除しました。", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
