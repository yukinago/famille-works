package jp.biz.prominent.app.entity;

import java.util.List;

import jp.biz.prominent.app.com.Constants;
import lombok.Data;

import org.apache.commons.lang3.StringUtils;

/**
 * ユーザー情報を表すエンティティクラス。
 * 
 * @author famille
 */
@Data
public class UserEntity {

	private Long uid;
	private String name;
	private String imageUrl;
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String keyword1;
	private String keyword2;
	private String keyword3;
	private String distance;
	private String longitude;
	private String latitude;
	private int searchType;
	private String follow;
	private String refollow;
	private String duplicate;
	private int thresholdNoFollow;
	private int thresholdNoTweet;
	private int dailyFollowMax;
	private String followDate;
	private int followCount;
	private int interval;
	private int range;

	private List<FollowerEntity> followerList;
	private List<TweetEntity> tweetList;

	/**
	 * フォローフラグを取得する。
	 * @return フォローフラグ
	 */
	public boolean checkFollow() {
		return StringUtils.equals(this.follow, Constants.FLG_ON);
	}

	/**
	 * リフォローフラグを取得する。
	 * @return フォローフラグ
	 */
	public boolean checkRefollow() {
		return StringUtils.equals(this.refollow, Constants.FLG_ON);
	}

	/**
	 * 重複可フラグを取得する。
	 * @return フォローフラグ
	 */
	public boolean checkDuplicate() {
		return StringUtils.equals(this.duplicate, Constants.FLG_ON);
	}
}
