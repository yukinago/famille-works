package jp.biz.prominent.app.com;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;

public class ComFileAppender extends FileAppender {

	/**
	 * 一意ファイル名
	 */
	public void setFile(String file) {
		String val = file.trim();
		SimpleDateFormat format = new SimpleDateFormat(val.split("#")[1]);
		fileName = val.split("#")[0] + format.format(new Date()) + val.split("#")[2];
	}
}
