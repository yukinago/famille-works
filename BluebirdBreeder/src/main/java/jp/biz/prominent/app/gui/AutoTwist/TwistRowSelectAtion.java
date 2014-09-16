package jp.biz.prominent.app.gui.AutoTwist;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class TwistRowSelectAtion implements ListSelectionListener {

	private AutoTwistPanel pnlAutoTwist;

	public TwistRowSelectAtion(AutoTwistPanel pnlAutoTwist) {
		this.pnlAutoTwist = pnlAutoTwist;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		log.info("【自動ツイート設定】ツイート内容の行選択処理。");

		DefaultTableModel tableModel = (DefaultTableModel) pnlAutoTwist.tblTemp.getModel();
		int selectedNo = pnlAutoTwist.tblTemp.getSelectedRow();
		if (selectedNo >= 0) {
			String template = tableModel.getValueAt(selectedNo, 2).toString();
			pnlAutoTwist.txtTemp.setText(template);
		}

		// データ保持
		pnlAutoTwist.backupTemplate();
	}
}
