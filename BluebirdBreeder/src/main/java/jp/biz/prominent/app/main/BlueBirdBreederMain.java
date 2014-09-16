package jp.biz.prominent.app.main;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import jp.biz.prominent.app.com.ToolInformation;
import jp.biz.prominent.app.com.db.FollowerAccess;
import jp.biz.prominent.app.com.db.TweetAccess;
import jp.biz.prominent.app.com.db.UserAccess;
import jp.biz.prominent.app.com.db.VersionUpAccess;
import jp.biz.prominent.app.com.inject.InjectionUtils;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.lang3.StringUtils;

/**
 * Main処理
 *
 * @author famille
 */
@CommonsLog
public class BlueBirdBreederMain {

	/**
	 * 起動Main
	 *
	 * @param args 実行時引数。使用しない。
	 */
	public static void main(String[] args) {
		log.info("アプリ起動。");
		log.info("[start] main.");

		String version = System.getProperty("tool.version");

		// 引数が省略された場合は、ノーマル版扱いとする
		if (StringUtils.isEmpty(version)) {
			ToolInformation.setVersion(ToolInformation.Version.NORMAL);
		}
		else {
			if (StringUtils.equals(version, "0")) {
				ToolInformation.setVersion(ToolInformation.Version.NORMAL);
			}
			else if (StringUtils.equals(version, "1")) {
				ToolInformation.setVersion(ToolInformation.Version.LITE);
			}
			else {
				log.error("不正な引数が指定されています：[" + version + "]");
				JOptionPane.showMessageDialog(null, "ツールのバージョンが判別できません。終了します。", "エラー", JOptionPane.ERROR_MESSAGE);
				// 終了する
				return;
			}
		}

		try {
			// ２重起動チェック
			final FileOutputStream fos = new FileOutputStream(new File("lock"));
			final FileChannel fc = fos.getChannel();
			final FileLock lock = fc.tryLock();

			// 既に起動されている場合
			if (lock == null) {
				log.warn("同ツールが既に起動されています。");
				JOptionPane.showMessageDialog(null, "同ツールが既に起動されています。", "エラー", JOptionPane.ERROR_MESSAGE);
				// 終了する
				return;
			}

			// ロック開放処理を登録する
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						if (lock != null && lock.isValid()) {
							lock.release();
						}
						fc.close();
						fos.close();
					}
					catch (Exception e) {
						log.error("Shut down hook error.", e);
					}
				}
			});

			// DB環境用意
			UserAccess user = InjectionUtils.getInstance(UserAccess.class);
			FollowerAccess follower = InjectionUtils.getInstance(FollowerAccess.class);
			TweetAccess tweet = InjectionUtils.getInstance(TweetAccess.class);

			// テーブル初期化
			user.createTable();
			follower.createTable();
			tweet.createTable();

			// 旧バージョンテーブルから新バージョンテーブルへの移行
			VersionUpAccess versionUp = InjectionUtils.getInstance(VersionUpAccess.class);
			versionUp.versionUp();

			// 主処理起動
			Image toolIcon = Toolkit.getDefaultToolkit().getImage("icon/Twitter_icon.png");
			MainFrame mainFrame = new MainFrame();
			mainFrame.setIconImage(toolIcon);
			mainFrame.setTitle(ToolInformation.getTitle());
			mainFrame.setSize(1024, 736);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setResizable(false);
			mainFrame.setVisible(true);
		}
		catch (Exception e) {
			JFrame mainFrame = new JFrame();
			JLabel label = new JLabel("例外が発生しました。" + e.getMessage());
			mainFrame.add(label);
			mainFrame.setSize(1024, 736);
			mainFrame.setVisible(true);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			log.error("Main init error.", e);
		}

		log.info("[end] main.");
	}
}
