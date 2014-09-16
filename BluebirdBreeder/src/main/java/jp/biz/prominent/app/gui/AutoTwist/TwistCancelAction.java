package jp.biz.prominent.app.gui.AutoTwist;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class TwistCancelAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動ツイート設定】取消処理。");

		AutoTwistPanel pnlAutoTwist = (AutoTwistPanel) ((JButton) e.getSource()).getParent();

		int ret = CommonUtil.showConfirm("設定内容を取り消します。よろしいですか？", false);

		if (ret == JOptionPane.YES_OPTION) {
			pnlAutoTwist.resetData();
		}
	}
}
