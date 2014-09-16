package jp.biz.prominent.app.gui.IdModify;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jp.biz.prominent.app.com.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class ModifyReferAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("【ユーザ修正】参照処理。");

		IdModifyPanel pnlIdModify = (IdModifyPanel) ((JButton) e.getSource()).getParent();

		JFileChooser imageChooser = new JFileChooser();
		imageChooser.setSelectedFile(new File(pnlIdModify.txtIcon.getText()));
		imageChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return true;
				return file.getName().toLowerCase().endsWith(".gif");
			}

			@Override
			public String getDescription() {
				return "GIFファイル(*.gif)";
			}
		});
		imageChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return true;
				return file.getName().toLowerCase().endsWith(".png");
			}

			@Override
			public String getDescription() {
				return "PNGファイル(*.png)";
			}
		});
		imageChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return true;
				return file.getName().toLowerCase().endsWith(".jpg");
			}

			@Override
			public String getDescription() {
				return "JPEGファイル(*.jpg)";
			}
		});
		int retvalue = imageChooser.showOpenDialog(pnlIdModify);
		if (retvalue == JFileChooser.APPROVE_OPTION) {
			pnlIdModify.txtIcon.setText(imageChooser.getSelectedFile().getPath());
			pnlIdModify.lblIconImg.setText(CommonUtil.getImgTxt1(imageChooser.getSelectedFile().getPath(), 110, 110));
		}
	}

}
