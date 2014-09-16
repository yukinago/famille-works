package jp.biz.prominent.app.gui.AutoTwist;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class TwistUnSelectAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動ツイート設定】全解除処理。");

		AutoTwistPanel pnlAutoTwist = (AutoTwistPanel) ((JButton) e.getSource()).getParent();

		for (int i = 0; i < pnlAutoTwist.tblTemp.getRowCount(); i++) {
			pnlAutoTwist.tblTemp.getModel().setValueAt(false, i, 0);
		}

	}
}
