package jp.biz.prominent.app.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import lombok.Cleanup;

public class CommonUtil {

	/**
	 * 必須チェック
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		boolean ret = false;

		if (str == null || "".equals(str.trim())) {
			ret = true;
		}
		return ret;
	}

	/**
	 * 型チェック
	 *
	 * @param str
	 * @return
	 */
	public static boolean isLong(String str) {
		boolean ret = false;

		if (isEmpty(str) == false) {
			try {
				long val = Long.parseLong(str);
				if (val >= 0) {
					ret = true;
				}
			}
			catch (Exception e) {
			}
		}

		return ret;
	}

	/**
	 * 型チェック
	 *
	 * @param str
	 * @return
	 */
	public static boolean isDecimal(String str) {
		boolean ret = false;

		if (isEmpty(str) == false) {
			try {
				BigDecimal val = new BigDecimal(str);
				if (val.compareTo(BigDecimal.ZERO) >= 0) {
					ret = true;
				}
			}
			catch (Exception e) {
			}
		}

		return ret;
	}

	/**
	 * Imgアイコンの内容設定:ローカル絶対パス
	 *
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getImgTxt1(String path, int width, int height) {
		String ret = "<html><body><img width=\"" + width + "\" height=\"" + height + "\" src=\"file:" + path + "\"/></body></html>";

		return ret;
	}

	/**
	 * Imgアイコンの内容設定:ローカル相対パス
	 *
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getImgTxt2(String path, int width, int height) {
		path = System.getProperty("user.dir") + path;
		String ret = "<html><body><img width=\"" + width + "\" height=\"" + height + "\" src=\"file:" + path + "\"/></body></html>";

		return ret;
	}

	/**
	 * Imgアイコンの内容設定：URL上
	 *
	 * @param str
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getUrlImgTxt(String str, int width, int height) {
		String ret = "<html><body><img width=\"" + width + "\" height=\"" + height + "\" src=\"" + str + "\"/></body></html>";

		return ret;
	}

	/**
	 * 現在時間取得 yyyy/MM/dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getSystemTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * N日前の日付判定
	 *
	 * @return
	 */
	public static boolean isPastTime(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -days);
		return date.before(cal.getTime());
	}

	/**
	 * N日前の日付判定
	 *
	 * @return
	 */
	public static boolean isPastTime(String time, int days) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			if (isEmpty(time)) {
				time = "2000/01/01 12:00:00";
			}
			Date date = format.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -days);
			return date.before(cal.getTime());
		}
		catch (Exception e) {
			//
		}

		return false;
	}

	/**
	 * 現在日付と同じか
	 *
	 * @return 同じ：Null、違う：現在日付
	 */
	public static String isSameDay(String diffDay) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String sysDate = format.format(new Date());
		if (sysDate.equals(diffDay)) {
			return null;
		}
		return sysDate;
	}

	/**
	 * 確認メッセージの表示
	 *
	 * @param message
	 * @param yesOption
	 * @return
	 */
	public static int showConfirm(String message, boolean yesOption) {
		if (yesOption) {
			return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "はい", "いいえ" }, "はい");
		}
		return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "はい", "いいえ" }, "いいえ");
	}

	/**
	 * 確認メッセージの表示
	 *
	 * @param message
	 * @param yesOption
	 * @return
	 */
	public static int showConfirm2(String message, boolean yesOption) {
		if (yesOption) {
			return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "はい", "いいえ", "キャンセル" }, "はい");
		}
		return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "はい", "いいえ", "キャンセル" }, "いいえ");
	}

	/**
	 * 確認メッセージの表示
	 *
	 * @param message
	 * @param yesOption
	 * @return
	 */
	public static int showConfirm3(String message, boolean yesOption) {
		if (yesOption) {
			return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "了解", "取消" }, "了解");
		}
		return JOptionPane.showOptionDialog(null, message, "警告", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "了解", "取消" }, "取消");
	}

	/**
	 * ファイルをコピーします。
	 *
	 * @param file コピー元ファイル
	 * @param newFileName コピー先ファイル名
	 */
	public static void fileCopy(File file, String newFileName) {

		FileChannel ifc = null;
		FileChannel ofc = null;
		try {
			// 入力元ファイルのストリームからチャネルを取得
			FileInputStream fis = new FileInputStream(file);
			ifc = fis.getChannel();

			// 出力先ファイルのチャネルを取得
			File outFile = new File(newFileName);
			FileOutputStream fos = new FileOutputStream(outFile);
			ofc = fos.getChannel();

			// バイトを転送します。
			ifc.transferTo(0, ifc.size(), ofc);

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (ifc != null) {
				try {
					// 入力チャネルを close します。
					ifc.close();
				}
				catch (IOException e) {
				}
			}
			if (ofc != null) {
				try {
					// 出力チャネルを close します。
					ofc.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	/**
	 * URLからイメージファイル保存
	 *
	 * @param urlStr
	 * @param newFileName
	 */
	public static void saveImg(String urlStr, String newFileName) {
		try {
			// ダウンロードする URL
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();

			@Cleanup
			InputStream in = conn.getInputStream();

			// 保存先
			File file = new File(newFileName);

			file.getParentFile().mkdirs();

			@Cleanup
			FileOutputStream out = new FileOutputStream(file, false);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}