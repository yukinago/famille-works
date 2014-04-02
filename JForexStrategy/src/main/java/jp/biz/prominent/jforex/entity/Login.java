package jp.biz.prominent.jforex.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;

/**
 * ログイン情報を表すエンティティ。
 * 
 * @author famille
 */
@Setter
@Getter
@ToString(callSuper = true)
public class Login {
	/** ログイン名 */
	private String loginName = "";
	/** パスワード（Base64エンコード） */
	private byte[] password = Base64.encodeBase64("".getBytes());
}
