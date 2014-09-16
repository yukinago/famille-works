package jp.biz.prominent.app.gui.IdModify;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class ModifyCancelAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【ユーザ修正】取消処理。");

		IdModifyPanel pnlIdModify = (IdModifyPanel) ((JButton) e.getSource()).getParent();

		int ret = CommonUtil.showConfirm("設定内容を取り消します。よろしいですか？", false);

		// YESの場合、再初期化
		if (ret == JOptionPane.YES_OPTION) {
			pnlIdModify.resetData();
		}
	}

}
