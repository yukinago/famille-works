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
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class AutoFollowAction extends AbstractAction {

	private HeaderPanel pnlHeader;
	protected JPanel parent;
	protected MainFrame mainFrame;
	private DefaultTableModel logTableModel;
	private JTable tblLogList;
	private JButton thisBtn;

	public AutoFollowAction(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【自動フォロー】フォロー処理。");

		pnlHeader = (HeaderPanel) ((JButton) e.getSource()).getParent();
		logTableModel = (DefaultTableModel) mainFrame.bodyPanel.tblLogList.getModel();
		tblLogList = mainFrame.bodyPanel.tblLogList;
		thisBtn = (JButton) e.getSource();

		// 未保存チェック
		if (mainFrame.bodyPanel.pnlAutoFollow.hasUnSaved()) {
			int ret = CommonUtil.showConfirm("保存していない設定があります。放棄してもよろしいですか？", false);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}

		// タスクを起動する場合
		if (pnlHeader.runAutoFollow == false) {
			ImageIcon followIcon1 = new ImageIcon("icon/AutoFollowStop.gif");
			ImageIcon followIcon2 = new ImageIcon("icon/AutoFollowStop.gif");
			thisBtn.setIcon(followIcon1);
			thisBtn.setRolloverIcon(followIcon2);
			pnlHeader.runAutoFollow = true;

			// ユーザ数分Task起動
			List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
			AutoFollowTask followTask;
			for (int i = 0; i < userInfoList.size(); i++) {
				followTask = new AutoFollowTask(mainFrame, pnlHeader, userInfoList.get(i));
				followTask.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("logWrite".equals(evt.getPropertyName())) {
							logTableModel.addRow(new Object[] { (String) evt.getNewValue() });
							Rectangle rect = tblLogList.getCellRect(logTableModel.getRowCount() - 1, 0, true);
							tblLogList.scrollRectToVisible(rect);
							mainFrame.bodyPanel.saveGuiLog((String) evt.getNewValue());
						}
						if ("userIcon".equals(evt.getPropertyName())) {
							pnlHeader.runIcon.setText((String) evt.getNewValue());
						}
						if ("userId".equals(evt.getPropertyName())) {
							pnlHeader.runId.setText(String.valueOf(evt.getNewValue()));
						}
					}
				});
				followTask.execute();
				userInfoList.get(i).setFollowTask(followTask);
			}
		}
		// タスクを中止する場合
		else {
			ImageIcon followIcon1 = new ImageIcon("icon/AutoFollowStart1.gif");
			ImageIcon followIcon2 = new ImageIcon("icon/AutoFollowStart2.gif");
			thisBtn.setIcon(followIcon1);
			thisBtn.setRolloverIcon(followIcon2);
			pnlHeader.runAutoFollow = false;

			// ユーザ数分Task中止
			List<UserInfo> userInfoList = mainFrame.bodyPanel.userInfoList;
			AutoFollowTask followTask;
			for (int i = 0; i < userInfoList.size(); i++) {
				followTask = userInfoList.get(i).getFollowTask();
				followTask.cancel(true);
			}
			JOptionPane.showMessageDialog(mainFrame, "自動フォロー処理が中止されました。", "情報", JOptionPane.INFORMATION_MESSAGE);
			log.info("【自動フォロー】自動フォロー処理が中止されました。");
		}
	}
}
