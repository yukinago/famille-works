package jp.biz.prominent.jforex.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import jp.biz.prominent.jforex.db.LoginAccess;
import jp.biz.prominent.jforex.entity.Login;
import jp.biz.prominent.jforex.gui.dialog.JForexStrategyMain;
import jp.co.famille.util.di.InjectionUtils;
import jp.co.famille.util.swing.ShowDialogUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * ログイン画面を表すパネルクラス。
 * 
 * @author famille
 */
@Getter
@Setter
public class LoginPanel extends JPanel {

	/** DBアクセス用のインスタンス */
	private LoginAccess access = InjectionUtils.getInstance(LoginAccess.class);

	private JTextField txtLoginName;
	private JPasswordField txtPassword;
	private JLabel lblLoginName;
	private JLabel lblPassword;
	private JCheckBox chkSaveLoginInfo;
	private JButton btnLogin;
	private JButton btnExit;

	/** メインフレーム */
	private static JForexStrategyMain mainFrame;

	/**
	 * ログイン画面を生成する。
	 * 
	 * @param frame メインフレーム
	 */
	public LoginPanel(JForexStrategyMain frame) {
		mainFrame = frame;

		// テーブルがなければ作成する
		access.createTable();

		// 前回ログイン情報を取得する
		Login login = access.select();

		// 前回ログイン情報を画面に展開する
		toDisplayItems(login);

		// ログインボタン押下時の処理を定義
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 現在のログイン情報を削除する
				access.delete();

				// 画面項目からログイン情報を取得する
				Login login = toEntity();

				// ログイン情報保存チェックがON状態の場合
				if (chkSaveLoginInfo.isSelected()) {
					access.insert(login);
				}

				// ログインを実行する
				mainFrame.login(login);
			}
		});

		// ログインボタンの活性状態を設定する
		setButtonEnabled();

		txtLoginName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// ログインボタンの活性状態を設定する
				setButtonEnabled();
			}
		});

		txtPassword.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// ログインボタンの活性状態を設定する
				setButtonEnabled();
			}
		});

		// 終了ボタン押下時の処理を定義
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 確認ダイアログにてOKボタンが押下された場合
				if (ShowDialogUtil.confirm("処理を終了します。よろしいですか。") == JOptionPane.OK_OPTION) {
					exit();
				}
			}
		});
	}

	/**
	 * ログインボタンの活性状態を設定する。
	 */
	private void setButtonEnabled() {
		btnLogin.setEnabled(
				StringUtils.isNotEmpty(txtLoginName.getText()) &&
						StringUtils.isNotEmpty(new String(txtPassword.getPassword())));
	}

	/**
	 * アプリケーションの終了時処理を実行する。
	 */
	public void exit() {
		// 現在のログイン情報を削除する
		access.delete();

		// 画面項目からログイン情報を取得する
		Login login = toEntity();

		// ログイン情報保存チェックがON状態の場合
		if (chkSaveLoginInfo.isSelected()) {
			access.insert(login);
		}

		System.exit(0);
	}

	/**
	 * ログイン情報を画面項目に展開する。
	 * 
	 * @param login ログイン情報
	 */
	private void toDisplayItems(Login login) {
		// 前回ログイン情報が初期値で生成する
		if (login == null) {
			login = new Login();
		}

		// 画面項目を生成する
		txtLoginName = new JTextField(12);
		txtPassword = new JPasswordField(12);
		btnLogin = new JButton("ログイン");
		btnExit = new JButton("終了");
		lblLoginName = new JLabel("ログイン名：", SwingConstants.RIGHT);
		lblPassword = new JLabel("パスワード：", SwingConstants.RIGHT);
		chkSaveLoginInfo = new JCheckBox("ログイン情報を保存する");

		// ログイン名
		txtLoginName.setText(login.getLoginName());
		// パスワード（Base64デコードする）
		txtPassword.setText(new String(Base64.decodeBase64(login.getPassword())));
		// ログイン情報保存チェックボックスをONにする
		chkSaveLoginInfo.setSelected(true);

		// レイアウトを設定する
		setItemLayout();
	}

	/**
	 * ログイン画面のレイアウトを設定する。
	 */
	private void setItemLayout() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		layout.setConstraints(lblLoginName, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		layout.setConstraints(txtLoginName, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		layout.setConstraints(lblPassword, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		layout.setConstraints(txtPassword, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		layout.setConstraints(chkSaveLoginInfo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		layout.setConstraints(btnLogin, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		layout.setConstraints(btnExit, gbc);

		add(lblLoginName);
		add(txtLoginName);
		add(lblPassword);
		add(txtPassword);
		add(chkSaveLoginInfo);
		add(btnLogin);
		add(btnExit);
	}

	/**
	 * ログイン情報を画面項目に展開する。
	 * 
	 * @return ログイン情報
	 */
	private Login toEntity() {
		Login login = new Login();

		// ログイン名
		login.setLoginName(txtLoginName.getText());
		// パスワード（Base64でエンコード）
		login.setPassword(Base64.encodeBase64(new String(txtPassword.getPassword()).getBytes(), false));

		return login;
	}
}
