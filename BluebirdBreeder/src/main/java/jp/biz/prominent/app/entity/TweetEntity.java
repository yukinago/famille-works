package jp.biz.prominent.app.entity;

import jp.biz.prominent.app.com.Constants;
import lombok.Data;

import org.apache.commons.lang3.StringUtils;

/**
 * 自動ツイート内容を表すエンティティ。
 * 
 * @author famille
 */
@Data
public class TweetEntity {

	private Long uid;
	private Integer tid;
	private String selected;
	private String createdTime;
	private String template;

	/**
	 * オブジェクトフラグを取得する。
	 * 
	 * @return オブジェクトフラグ
	 */
	public boolean checkObjflg() {
		return StringUtils.equals(this.selected, Constants.FLG_ON);
	}
}
