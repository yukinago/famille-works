package jp.biz.prominent.jforex.gui.dialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import jp.biz.prominent.jforex.entity.Login;
import jp.biz.prominent.jforex.gui.panel.LoginPanel;
import jp.biz.prominent.jforex.gui.panel.StrategyConfigPanel;
import jp.co.famille.util.di.InjectionUtils;
import jp.co.famille.util.swing.ShowDialogUtil;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.codec.binary.Base64;

import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;

/**
 * JForexストラテジーアプリケーションのメインクラス。
 * 
 * @author famille
 */
@CommonsLog
public class JForexStrategyMain extends JFrame {

	/** ログイン画面 */
	private LoginPanel loginPanel;
	/** ストラテジー設定 */
	private StrategyConfigPanel strategyConfigPanel;

	/** JForexクライアント */
	private IClient client;

	/** 接続先JNLPのURL */
	private static final String JNLP_URL = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";

	/**
	 * ログインダイアログを表示する。
	 * 
	 * @param args 実行時引数。使用しない。 
	 */
	public static void main(String[] args) {
		try {
			// インジェクションを準備する
			InjectionUtils.setConfigFile("mybatis-config.xml");

			JForexStrategyMain dialog = new JForexStrategyMain();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ダイアログを生成する。
	 */
	public JForexStrategyMain() {
		setTitle("ログイン画面");
		setSize(300, 120);

		// 画面中央に表示する
		setLocationRelativeTo(null);
		//
		setResizable(false);

		// クライアントを初期化する
		initClient();

		loginPanel = new LoginPanel(this);
		strategyConfigPanel = new StrategyConfigPanel(client);

		// ウインドウクローズ時の処理を設定する
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				// システム終了処理を実行する
				strategyConfigPanel.exit();
				loginPanel.exit();
				exit();
			}
		});

		getContentPane().add(loginPanel);
		// getContentPane().add(strategyConfigPanel);

		loginPanel.setVisible(true);
	}

	/**
	 * クライアントの初期化を行う。
	 */
	private void initClient() {
		try {
			client = ClientFactory.getDefaultInstance();
			// set the listener that will receive system events
			client.setSystemListener(new ISystemListener() {
				@Override
				public void onStart(long processId) {
					log.info("Strategy started: " + processId);
				}

				@Override
				public void onStop(long processId) {
					log.info("Strategy stopped: " + processId);
					if (client.getStartedStrategies().isEmpty()) {
						System.exit(0);
					}
				}

				@Override
				public void onConnect() {
					log.info("Connected");
				}

				@Override
				public void onDisconnect() {
					log.warn("Disconnected");
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * JForexサーバーにログインする。
	 * 
	 * @param login ログイン情報
	 */
	public void login(Login login) {
		try {
			// ログインを実行する
			client.connect(
					JNLP_URL,
					login.getLoginName(),
					new String(Base64.decodeBase64(login.getPassword())));

			// コネクションが確立できるまで待機する
			for (int i = 0; i < 10; i++) {
				if (client.isConnected()) {
					break;
				}
				Thread.sleep(1000);
			}

			// コネクションが確立できた場合は、画面の切り替えを行う
			if (client.isConnected()) {
				this.remove(loginPanel);
				this.add(strategyConfigPanel);
				strategyConfigPanel.setVisible(true);
				setTitle("ストラテジー設定／実行画面");
				setSize(755, 280);
				this.setVisible(true);
			}
			// コネクションが確立できない場合は、タイムアウトとして終了する
			else {
				ShowDialogUtil.info("ログインにタイムアウトしました。終了します。");
				System.exit(1);
			}
		}
		catch (Exception e) {
			ShowDialogUtil.info("ログインに失敗しました。：" + e.getLocalizedMessage());
		}
	}

	/**
	 * アプリケーションの終了時処理を実行する。
	 */
	public void exit() {
		// 接続中なら切断する
		if (client != null && client.isConnected()) {
			client.disconnect();
		}

		// 処理を終了する
		System.exit(0);
	}
}
