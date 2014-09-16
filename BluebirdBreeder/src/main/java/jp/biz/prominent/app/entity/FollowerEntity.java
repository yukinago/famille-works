package jp.biz.prominent.app.entity;

import jp.biz.prominent.app.com.Constants;
import lombok.Data;

import org.apache.commons.lang3.StringUtils;

/**
 * フォロワーを表すエンティティ。
 * 
 * @author famille
 */
@Data
public class FollowerEntity {

	private Long uid;
	private Long fid;
	private String unfollow;
	private String status;
	private String name;
	private String imageUrl;
	private String followTime;

	/**
	 * アンフォローフラグを取得する。
	 * 
	 * @return アンフォローフラグ
	 */
	public boolean checkUnfollow() {
		return StringUtils.equals(this.unfollow, Constants.FLG_ON);
	}
}
