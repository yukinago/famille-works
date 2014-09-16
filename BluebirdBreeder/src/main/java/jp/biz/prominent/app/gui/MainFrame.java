package jp.biz.prominent.app.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.ToolInformation;
import jp.biz.prominent.app.gui.Body.BodyPanel;
import jp.biz.prominent.app.gui.Header.HeaderPanel;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class MainFrame extends JFrame {

	public HeaderPanel headPanel;
	public BodyPanel bodyPanel;

	/**
	 * コンストラクタ
	 *
	 * @throws Exception
	 */
	public MainFrame() throws Exception {

		getContentPane().setLayout(null);

		headPanel = new HeaderPanel(this);
		headPanel.setSize(1024, 50);
		headPanel.setBackground(Constants.BG_COLOR);
		getContentPane().add(headPanel);

		bodyPanel = new BodyPanel(this);
		bodyPanel.setSize(1024, 680);
		bodyPanel.setLocation(0, 54);
		getContentPane().add(bodyPanel);

		this.setTitle(ToolInformation.getTitle());
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setBackground(Constants.BG_COLOR);

		// 閉じるイベント
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				checkBeforExit();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	/**
	 * 閉じる前のチェック
	 */
	private void checkBeforExit() {
		log.info("アプリ終了。");

		if (bodyPanel.pnlIdRegist.hasUnSaved()) {
			int ret = CommonUtil.showConfirm("ユーザ登録画面に保存していない設定があります。Bluebird Breederを終了すると設定が失われます。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		else if (bodyPanel.pnlIdModify.hasUnSaved()) {
			int ret = CommonUtil.showConfirm("ユーザ修正画面に保存していない設定があります。Bluebird Breederを終了すると設定が失われます。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		else if (bodyPanel.pnlAutoFollow.hasUnSaved()) {
			int ret = CommonUtil.showConfirm("自動フォロー設定画面に保存していない設定があります。Bluebird Breederを終了すると設定が失われます。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		else if (bodyPanel.pnlAutoTwist.hasUnSaved()) {
			int ret = CommonUtil.showConfirm("自動ツイート設定画面に保存していない設定があります。Bluebird Breederを終了すると設定が失われます。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		else if (headPanel.runAutoFollow) {
			int ret = CommonUtil.showConfirm("自動フォロー中です。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		else if (headPanel.runAutoTwist) {
			int ret = CommonUtil.showConfirm("自動ツィート中です。終了してもよろしいですか？", true);
			if (ret == JOptionPane.NO_OPTION) {
				return;
			}
		}
		this.dispose();
	}
}
