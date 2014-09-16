package jp.biz.prominent.app.gui.Body;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.biz.prominent.app.com.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;

/**
 * TODO 未使用
 *
 * @author famille
 *
 */
@CommonsLog
public class TabChangeAction implements ChangeListener {

	private BodyPanel bodyPanel;

	public TabChangeAction(BodyPanel bodyPanel) {
		this.bodyPanel = bodyPanel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		log.info("機能タブ変更。");

		boolean hasChange = false;
		if (bodyPanel.pnlFuncTab.getSelectedIndex() != bodyPanel.tabIndex) {
			switch (bodyPanel.tabIndex) {
				case 0:
					hasChange = bodyPanel.pnlIdRegist.hasUnSaved();
					break;
				case 1:
					hasChange = bodyPanel.pnlIdModify.hasUnSaved();
					break;
				case 2:
					hasChange = bodyPanel.pnlAutoFollow.hasUnSaved();
					break;
				case 3:
					hasChange = bodyPanel.pnlAutoTwist.hasUnSaved();
					break;
				default:
					break;
			}
			if (hasChange) {
				int ret = CommonUtil.showConfirm("保存していない設定があります。放棄してもよろしいですか？", false);
				if (ret == JOptionPane.YES_OPTION) {
					switch (bodyPanel.tabIndex) {
						case 0:
							bodyPanel.pnlIdRegist.resetData();
							break;
						case 1:
							bodyPanel.pnlIdModify.resetData();
							break;
						case 2:
							bodyPanel.pnlAutoFollow.resetData();
							break;
						case 3:
							bodyPanel.pnlAutoTwist.resetData();
							break;
						default:
							break;
					}
				}
				else {
					// 旧TABの選択へ戻す
					bodyPanel.pnlFuncTab.setSelectedIndex(bodyPanel.tabIndex);
					return;
				}

			}
			bodyPanel.tabIndex = bodyPanel.pnlFuncTab.getSelectedIndex();
		}
	}

}
