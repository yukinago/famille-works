package jp.biz.prominent.app.gui.AutoTwist;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.gui.PopupMenuMouseListener;
import jp.biz.prominent.app.entity.TweetEntity;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.lang3.StringUtils;

/**
 * 自動ツイート設定画面
 *
 * @author famille
 */
@SuppressWarnings("serial")
@CommonsLog
public class AutoTwistPanel extends JPanel {
	public UserInfo userInfo;

	private JScrollPane pnlTemp;
	private JLabel lblTxtTemp;
	private JLabel lblTime1;
	private JLabel lblCmtTime1;
	private JLabel lblTime2;
	private JLabel lblCmtTime2;
	private JPanel pnlSetting;
	private JScrollPane scrollTxtTemp;
	private DefaultTableModel tableModel;

	public JTable tblTemp;
	public JTextArea txtTemp;
	public JTextField txtTime1;
	public JTextField txtTime2;
	public JButton btnSelAll;
	public JButton btnUnSelAll;
	public JButton btnSelDel;
	public JButton btnAdd;
	public JButton btnUpdate;
	public JButton btnSave;
	public JButton btnCancel;

	private String backTemp = "";
	private String backTime1 = "";
	private String backTime2 = "";

	public boolean unSaveFlg = false;

	/**
	 * コンストラクタ
	 */
	public AutoTwistPanel() {
		log.info("【自動ツイート設定】初期処理。");

		setLayout(null);
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(10, 10, 580, 600);
		this.setBackground(Constants.BG_COLOR);

		// ツイート内容一覧部分
		pnlTemp = new JScrollPane();
		pnlTemp.setBounds(12, 10, 560, 270);
		pnlTemp.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " ツイート内容一覧 ", TitledBorder.LEFT, TitledBorder.TOP));
		pnlTemp.setBackground(Constants.BG_COLOR);
		pnlTemp.getViewport().setBackground(Constants.BG_COLOR);

		tblTemp = new JTable() {
			@Override
			public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
				Component c = super.prepareRenderer(tcr, row, column);
				if (isRowSelected(row)) {
					c.setForeground(getSelectionForeground());
					c.setBackground(getSelectionBackground());
				}
				else {
					c.setForeground(getForeground());
					c.setBackground((row % 2 == 0) ? Constants.EVEN_COLOR : getBackground());
				}
				return c;
			}
		};
		tableModel = new DefaultTableModel(new String[] { "対象", "作成日時", "ツイート内容" }, 0) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Boolean.class, Object.class, Object.class };

			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { true, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tblTemp.setModel(tableModel);
		tblTemp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblTemp.getColumnModel().getColumn(0).setPreferredWidth(45);
		tblTemp.getColumnModel().getColumn(0).setMinWidth(45);
		tblTemp.getColumnModel().getColumn(0).setMaxWidth(45);
		tblTemp.getColumnModel().getColumn(1).setResizable(false);
		tblTemp.getColumnModel().getColumn(1).setPreferredWidth(120);
		tblTemp.getColumnModel().getColumn(1).setMinWidth(120);
		tblTemp.getColumnModel().getColumn(1).setMaxWidth(120);
		tblTemp.getColumnModel().getColumn(2).setResizable(false);
		tblTemp.getColumnModel().getColumn(2).setPreferredWidth(300);
		tblTemp.getColumnModel().getColumn(2).setMinWidth(100);
		tblTemp.getColumnModel().getColumn(2).setMaxWidth(1000);
		tblTemp.setBounds(12, 44, 500, 485);
		tblTemp.setSelectionBackground(Color.ORANGE);
		pnlTemp.setViewportView(tblTemp);
		add(pnlTemp);

		txtTemp = new JTextArea();
		txtTemp.setWrapStyleWord(true);
		txtTemp.setLineWrap(true);
		txtTemp.setBackground(SystemColor.info);
		txtTemp.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollTxtTemp = new JScrollPane(txtTemp);
		scrollTxtTemp.setBounds(12, 330, 560, 87);
		add(scrollTxtTemp);

		btnUpdate = new JButton("選択修正");
		btnUpdate.setBounds(360, 420, 104, 20);
		add(btnUpdate);

		btnAdd = new JButton("新規追加");
		btnAdd.setBounds(468, 420, 104, 20);
		add(btnAdd);

		btnSelDel = new JButton("選択削除");
		btnSelDel.setBounds(468, 285, 104, 20);
		add(btnSelDel);

		lblTxtTemp = new JLabel("ツイート内容作成：");
		lblTxtTemp.setHorizontalAlignment(SwingConstants.LEFT);
		lblTxtTemp.setBounds(12, 311, 130, 20);
		add(lblTxtTemp);

		btnSelAll = new JButton("全選択");
		btnSelAll.setBounds(12, 285, 80, 20);
		add(btnSelAll);

		btnUnSelAll = new JButton("全解除");
		btnUnSelAll.setBounds(100, 285, 80, 20);
		add(btnUnSelAll);

		pnlSetting = new JPanel();
		pnlSetting.setLayout(null);
		pnlSetting.setBounds(12, 456, 560, 80);
		pnlSetting.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " 自動ツィート設定 ", TitledBorder.LEFT, TitledBorder.TOP));
		pnlSetting.setBackground(Constants.BG_COLOR);
		add(pnlSetting);

		lblTime1 = new JLabel("ツィート間隔：");
		lblTime1.setBounds(12, 20, 100, 20);
		pnlSetting.add(lblTime1);
		lblTime1.setHorizontalAlignment(SwingConstants.RIGHT);

		lblCmtTime1 = new JLabel("（１０分～３６００分）");
		lblCmtTime1.setBounds(220, 20, 150, 20);
		pnlSetting.add(lblCmtTime1);
		lblCmtTime1.setHorizontalAlignment(SwingConstants.LEFT);

		txtTime1 = new JTextField();
		txtTime1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTime1.setBackground(SystemColor.info);
		txtTime1.setBounds(130, 20, 80, 20);
		pnlSetting.add(txtTime1);
		txtTime1.setColumns(10);

		lblTime2 = new JLabel("ゆらぎ：");
		lblTime2.setBounds(12, 50, 100, 20);
		pnlSetting.add(lblTime2);
		lblTime2.setHorizontalAlignment(SwingConstants.RIGHT);

		lblCmtTime2 = new JLabel("（０秒～１８０秒）");
		lblCmtTime2.setBounds(220, 50, 150, 20);
		pnlSetting.add(lblCmtTime2);
		lblCmtTime2.setHorizontalAlignment(SwingConstants.LEFT);

		txtTime2 = new JTextField();
		txtTime2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTime2.setBackground(SystemColor.info);
		txtTime2.setBounds(130, 50, 80, 20);
		pnlSetting.add(txtTime2);
		txtTime2.setColumns(10);

		btnSave = new JButton("保存");
		btnSave.setBounds(170, 550, 70, 20);
		add(btnSave);

		btnCancel = new JButton("取消");
		btnCancel.setBounds(269, 550, 70, 20);
		add(btnCancel);

		txtTemp.addMouseListener(new PopupMenuMouseListener());
		txtTime1.addMouseListener(new PopupMenuMouseListener());
		txtTime2.addMouseListener(new PopupMenuMouseListener());
	}

	/**
	 * 選択されたユーザ情報の表示
	 *
	 * @param userInfo
	 */
	public void setUserData(UserInfo userInfo) {

		this.userInfo = userInfo;
		List<TweetEntity> tweetEntityList = userInfo.getUserEntity().getTweetList();

		// 一覧クリア
		tableModel.setRowCount(0);

		// ツイート内容一覧
		for (TweetEntity tweetEntity : tweetEntityList) {
			tableModel.addRow(new Object[] { StringUtils.equals(tweetEntity.getSelected(), Constants.FLG_ON), tweetEntity.getCreatedTime(), tweetEntity.getTemplate() });
		}

		this.txtTemp.setText("");
		this.txtTime1.setText(Integer.toString(userInfo.getUserEntity().getInterval()));
		this.txtTime2.setText(Integer.toString(userInfo.getUserEntity().getRange()));

	}

	/**
	 * データ初期戻す
	 */
	public void resetData() {
		this.txtTemp.setText(backTemp);
		this.txtTime1.setText(backTime1);
		this.txtTime2.setText(backTime2);

		unSaveFlg = false;
	}

	/**
	 * 修正前のデータ保持
	 */
	public void backupData() {
		backTemp = this.txtTemp.getText();
		backTime1 = this.txtTime1.getText();
		backTime2 = this.txtTime2.getText();
	}

	/**
	 * 未保存チェック
	 *
	 * @return
	 */
	public boolean hasUnSaved() {
		boolean hasUnsaved = false;

		hasUnsaved = hasUnsaved || unSaveFlg;

		if (backTemp.equals(txtTemp.getText()) == false) {
			hasUnsaved = true;
		}
		if (backTime1.equals(txtTime1.getText()) == false) {
			hasUnsaved = true;
		}
		if (backTime2.equals(txtTime2.getText()) == false) {
			hasUnsaved = true;
		}

		return hasUnsaved;
	}

	/**
	 * データ保存
	 */
	public void saveData() {
		// ツイート内容一覧
		List<TweetEntity> tweetEntityList = userInfo.getUserEntity().getTweetList();
		TweetEntity tweetEntity = null;
		for (int i = 0; i < tweetEntityList.size(); i++) {
			tweetEntity = tweetEntityList.get(i);
			tweetEntity.setSelected(((Boolean) tableModel.getValueAt(i, 0)) ? Constants.FLG_ON : Constants.FLG_OFF);
		}
		// ツィート間隔
		userInfo.getUserEntity().setInterval(Integer.parseInt(this.txtTime1.getText()));
		// ゆらぎ
		userInfo.getUserEntity().setRange(Integer.parseInt(this.txtTime2.getText()));
	}

	/**
	 * 修正前のテンプレート保持
	 */
	public void backupTemplate() {
		backTemp = this.txtTemp.getText();
	}
}