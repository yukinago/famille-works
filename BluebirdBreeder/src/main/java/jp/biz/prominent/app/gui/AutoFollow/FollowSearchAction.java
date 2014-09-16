package jp.biz.prominent.app.gui.AutoFollow;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserUtil;
import jp.biz.prominent.app.com.gui.CommonAction;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;
import twitter4j.TwitterException;
import twitter4j.User;

@SuppressWarnings("serial")
@CommonsLog
public class FollowSearchAction extends CommonAction {

	private AutoFollowPanel pnlAutoFollow;

	public FollowSearchAction(MainFrame mainFrame) {
		super.mainFrame = mainFrame;
	}

	@Override
	protected void doEvent() {
		log.info("【自動フォロー設定】検索処理。");

		pnlAutoFollow = mainFrame.bodyPanel.pnlAutoFollow;

		// 検索タイプ
		int searchType = 1;
		if (pnlAutoFollow.rdbtnFollow.isSelected()) {
			searchType = 1;
		}
		else if (pnlAutoFollow.rdbtnForbi.isSelected()) {
			searchType = 2;
		}
		else if (pnlAutoFollow.rdbtnKey.isSelected()) {
			searchType = 3;
		}
		else if (pnlAutoFollow.rdbtnAnd.isSelected()) {
			searchType = 4;
		}
		pnlAutoFollow.searchType = searchType;

		// 入力チェック
		String key1 = pnlAutoFollow.txtKey1.getText();
		String key2 = pnlAutoFollow.txtKey2.getText();
		String key3 = pnlAutoFollow.txtKey3.getText();
		double radius = 0;
		double longitude = 0;
		double latitude = 0;
		try {
			radius = Double.parseDouble(pnlAutoFollow.txtRadius.getText());
		}
		catch (NumberFormatException ex) {
			//
		}
		try {
			longitude = Double.parseDouble(pnlAutoFollow.txtLongitude.getText());
		}
		catch (NumberFormatException ex) {
			//
		}
		try {
			latitude = Double.parseDouble(pnlAutoFollow.txtLatitude.getText());
		}
		catch (NumberFormatException ex) {
			//
		}
		// KEY検索とAND検索の場合
		if (searchType == 3 || searchType == 4) {
			if ("".equals(key1)) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "キーワードが設定されていません。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		// AND検索の場合
		if (searchType == 4) {
			if (radius == 0) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "範囲に数字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (longitude == 0) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "経度に数字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (radius == 0) {
				super.dialog.cancel();
				JOptionPane.showMessageDialog(mainFrame, "緯度に数字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		List<FollowerEntity> followerEntityList = null;
		List<User> friendsList = new ArrayList<User>();
		List<User> tweetUserList = new ArrayList<User>();

		// Localフォロー済
		followerEntityList = pnlAutoFollow.userInfo.getUserEntity().getFollowerList();

		// Serverフォロー済
		friendsList = pnlAutoFollow.userInfo.getServerFriendsList(mainFrame);

		// 検索：フォロー対象
		if (searchType == 3 || searchType == 4) {

			// ローカルフォロー済とフォロー禁止は対象外
			List<Long> exceptObjs = new ArrayList<Long>();
			for (FollowerEntity followerEntity : followerEntityList) {
				if (Constants.FOLLOW_LOCAL.equals(followerEntity.getStatus()) || Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
					exceptObjs.add(followerEntity.getFid());
				}
			}

			// サーバ側フォロー済は対象外
			for (int i = 0; i < friendsList.size(); i++) {
				exceptObjs.add(friendsList.get(i).getId());
			}

			// 指定条件の検索
			try {
				tweetUserList = pnlAutoFollow.userInfo.searchByKeys(key1, key2, key3, radius, longitude, latitude, searchType, mainFrame, exceptObjs);
			}
			catch (TwitterException e) {
				super.dialog.cancel();
				UserUtil.checkException(e, mainFrame);
			}
		}

		// GUIの表示
		DefaultTableModel autoFollowTblModel = (DefaultTableModel) pnlAutoFollow.tblFollower.getModel();
		// 既存データクリア
		autoFollowTblModel.setRowCount(0);

		// 検索：フォロー済
		if (searchType == 1) {
			List<Long> localFollowIds = new ArrayList<Long>();
			// ローカルフォロー済
			for (FollowerEntity followerEntity : followerEntityList) {
				if (Constants.FOLLOW_LOCAL.equals(followerEntity.getStatus())) {
					autoFollowTblModel.addRow(new Object[] { followerEntity.checkUnfollow(), followerEntity.getStatus(), followerEntity.getFid(), followerEntity.getName(), followerEntity.getImageUrl() });
					localFollowIds.add(followerEntity.getFid());
				}
				if (Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
					localFollowIds.add(followerEntity.getFid());
				}
			}
			// Serverフォロー済
			for (User user : friendsList) {
				// Local分除外
				if (localFollowIds.contains(user.getId()) == false) {
					autoFollowTblModel.addRow(new Object[] { false, Constants.FOLLOW_SERVER, user.getId(), user.getName(), CommonUtil.getUrlImgTxt(user.getProfileImageURL().toString(), 35, 35) });
				}
			}
		}
		// 検索：禁止リスト
		else if (searchType == 2) {
			for (FollowerEntity followerEntity : followerEntityList) {
				if (Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
					autoFollowTblModel.addRow(new Object[] { followerEntity.checkUnfollow(), followerEntity.getStatus(), followerEntity.getFid(), followerEntity.getName(), followerEntity.getImageUrl() });
				}
			}
		}
		// 検索：フォロー対象
		else if (searchType == 3 || searchType == 4) {
			for (User user : tweetUserList) {
				autoFollowTblModel.addRow(new Object[] { false, Constants.FOLLOW_NOT, user.getId(), user.getName(), CommonUtil.getUrlImgTxt(user.getProfileImageURL().toString(), 35, 35) });
			}
			pnlAutoFollow.unSaveFlg = true;
		}

		// ヒット情報
		pnlAutoFollow.lblHitInfo.setText("ヒット件数：" + autoFollowTblModel.getRowCount());

		super.dialog.cancel();
		JOptionPane.showMessageDialog(mainFrame, "検索しました。（結果：" + autoFollowTblModel.getRowCount() + "件）", "情報", JOptionPane.INFORMATION_MESSAGE);
	}
}
