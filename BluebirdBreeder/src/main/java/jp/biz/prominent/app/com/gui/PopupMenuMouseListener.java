package jp.biz.prominent.app.com.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

public class PopupMenuMouseListener implements MouseListener {
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePopup(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private void mousePopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// ポップアップメニューを表示する
			JComponent c = (JComponent) e.getSource();
			c.requestFocus();
			showPopup(c, e.getX(), e.getY());
			e.consume();
		}
	}

	protected void showPopup(JComponent c, int x, int y) {
		JPopupMenu pmenu = new JPopupMenu();

		ActionMap am = c.getActionMap();

		Action cut = am.get(DefaultEditorKit.cutAction);
		addMenu(pmenu, "切り取り(X)", cut, 'X', KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));

		Action copy = am.get(DefaultEditorKit.copyAction);
		addMenu(pmenu, "コピー(C)", copy, 'C', KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));

		Action paste = am.get(DefaultEditorKit.pasteAction);
		addMenu(pmenu, "貼り付け(V)", paste, 'V', KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));

		Action all = am.get(DefaultEditorKit.selectAllAction);
		addMenu(pmenu, "すべて選択(A)", all, 'A', KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));

		pmenu.show(c, x, y);
	}

	protected void addMenu(JPopupMenu pmenu, String text, Action action, int mnemonic, KeyStroke ks) {
		if (action != null) {
			JMenuItem mi = pmenu.add(action);
			if (text != null) {
				mi.setText(text);
			}
			if (mnemonic != 0) {
				mi.setMnemonic(mnemonic);
			}
			if (ks != null) {
				mi.setAccelerator(ks);
			}
		}
	}

}
