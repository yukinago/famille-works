package jp.biz.prominent.app.gui.AutoFollow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class FollowCancelAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動フォロー設定】取消処理。");

		AutoFollowPanel pnlAutoFollow = (AutoFollowPanel) ((JButton) e.getSource()).getParent();

		int ret = CommonUtil.showConfirm("設定内容を取り消します。よろしいですか？", false);

		// YESの場合、再初期化
		if (ret == JOptionPane.YES_OPTION) {
			pnlAutoFollow.resetData();
		}

	}

}
