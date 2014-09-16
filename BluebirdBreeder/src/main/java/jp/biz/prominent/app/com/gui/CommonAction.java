package jp.biz.prominent.app.com.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import jp.biz.prominent.app.gui.MainFrame;

@SuppressWarnings("serial")
public class CommonAction extends AbstractAction {

	protected MainFrame mainFrame;
	public ProgressDialog<String> dialog;
	public boolean selfFlg = true;
	public boolean saveOkFlg = false;

	@Override
	public void actionPerformed(ActionEvent e) {

		// 処理中
		dialog = new ProgressDialog<String>((Frame) SwingUtilities.getAncestorOfClass(Frame.class, mainFrame), true);
		dialog.setLocationRelativeTo(mainFrame);
		dialog.submit(new Callable<String>() {
			public String call() throws Exception {
				try {
					doEvent();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return "success";
			}
		});
		dialog.setVisible(true);
	}

	// 拡張要
	protected void doEvent() {

	}

}
