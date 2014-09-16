package jp.biz.prominent.logging;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.FileAppender;

/**
 * ファイル名にタイムスタンプを設定可能なアペンダ。
 * 
 * @author famille
 */
public class TimestampFileAppender extends FileAppender {

	/**
	 * 一意ファイル名を設定する
	 */
	@Override
	public void setFile(String filePattern) {
		if (StringUtils.isEmpty(filePattern)) {
			super.setFile(filePattern);
			return;
		}

		// 前後の空白を取り除く
		filePattern = StringUtils.trim(filePattern);

		if (StringUtils.countMatches(filePattern, "#") == 2) {
			try {
				String prefix = StringUtils.substringBefore(filePattern, "#");
				String suffix = StringUtils.substringAfterLast(filePattern, "#");
				String datePattern = StringUtils.substringBetween(filePattern, "#", "#");

				fileName = prefix + DateFormatUtils.format(Calendar.getInstance(), datePattern) + suffix;
			}
			catch (RuntimeException e) {
				super.setFile(filePattern);
			}
		}
		else {
			super.setFile(filePattern);
		}
	}
}
