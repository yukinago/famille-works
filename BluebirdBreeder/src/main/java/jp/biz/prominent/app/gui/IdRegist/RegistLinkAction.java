package jp.biz.prominent.app.gui.IdRegist;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class RegistLinkAction extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		log.info("【ユーザ登録】リンク起動。");

		try {
			Desktop.getDesktop().browse(new URI("https://dev.twitter.com/apps/new"));

		}
		catch (Exception ex) {
			log.error("Apps link open error.", ex);
		}
	}

}
