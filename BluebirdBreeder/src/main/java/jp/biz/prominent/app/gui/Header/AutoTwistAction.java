package jp.biz.prominent.app.gui.Header;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class AutoTwistAction extends AbstractAction {

	private HeaderPanel pnlHeader;
	protected MainFrame mainFrame;
	private DefaultTableModel logTableModel;
	private JTable tblLogList;
	private JButton thisBtn;

	public AutoTwistAction(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動ツイート】ツイート処理。");

		pnlHeader = (HeaderPanel) ((JButton) e.getSource()).getParent();
		logTableModel = (DefaultTableModel) mainFrame.bodyPanel.tblLogList.getModel();
		tblLogList = mainFrame.bodyPanel.tblLogList;
		thisBtn = (JButton) e.getSource();

		// 未保存チェック
		if (mainFrame.bodyPanel.pnlAutoTwist.unSaveFlg) {
			int ret = CommonUtil.showConfirm("保存していない設定があります。放棄してもよろしいですか？", false);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}

		// タスクを起動する場合
		if (pnlHeader.runAutoTwist == false) {
			ImageIcon twistIcon1 = new ImageIcon("icon/AutoTwistStop.gif");
			ImageIcon twistIcon2 = new ImageIcon("icon/AutoTwistStop.gif");
			thisBtn.setIcon(twistIcon1);
			thisBtn.setRolloverIcon(twistIcon2);
			pnlHeader.runAutoTwist = true;

			// ユーザ数分Task起動
			List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
			AutoTwistTask twistTask;
			for (int i = 0; i < userInfoList.size(); i++) {
				twistTask = new AutoTwistTask(mainFrame, pnlHeader, userInfoList.get(i));
				twistTask.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						// ログ出力
						if ("logWrite".equals(evt.getPropertyName())) {
							logTableModel.addRow(new Object[] { (String) evt.getNewValue() });
							Rectangle rect = tblLogList.getCellRect(logTableModel.getRowCount() - 1, 0, true);
							tblLogList.scrollRectToVisible(rect);
							mainFrame.bodyPanel.saveGuiLog((String) evt.getNewValue());
						}
					}
				});
				twistTask.execute();
				userInfoList.get(i).setTwistTask(twistTask);
			}
		}
		// タスクを中止する場合
		else {
			ImageIcon twistIcon1 = new ImageIcon("icon/AutoTwistStart1.gif");
			ImageIcon twistIcon2 = new ImageIcon("icon/AutoTwistStart2.gif");
			thisBtn.setIcon(twistIcon1);
			thisBtn.setRolloverIcon(twistIcon2);
			pnlHeader.runAutoTwist = false;

			// ユーザ数分Task中止
			List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
			AutoTwistTask twistTask;
			for (int i = 0; i < userInfoList.size(); i++) {
				twistTask = userInfoList.get(i).getTwistTask();
				twistTask.cancel(true);
			}
			JOptionPane.showMessageDialog(mainFrame, "自動ツィート処理が中止されました。", "情報", JOptionPane.INFORMATION_MESSAGE);
			log.info("【自動ツイート】自動ツィート処理が中止されました。");
		}
	}
}
