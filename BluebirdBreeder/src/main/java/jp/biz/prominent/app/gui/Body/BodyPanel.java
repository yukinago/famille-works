package jp.biz.prominent.app.gui.Body;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.UserUtil;
import jp.biz.prominent.app.gui.MainFrame;
import jp.biz.prominent.app.gui.AutoFollow.AutoFollowPanel;
import jp.biz.prominent.app.gui.AutoFollow.FollowCancelAction;
import jp.biz.prominent.app.gui.AutoFollow.FollowDeleteAction;
import jp.biz.prominent.app.gui.AutoFollow.FollowSaveAction;
import jp.biz.prominent.app.gui.AutoFollow.FollowSearchAction;
import jp.biz.prominent.app.gui.AutoFollow.FollowUnFollowAction;
import jp.biz.prominent.app.gui.AutoTwist.AutoTwistPanel;
import jp.biz.prominent.app.gui.AutoTwist.TwistAddAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistCancelAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistDeleteAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistRowSelectAtion;
import jp.biz.prominent.app.gui.AutoTwist.TwistSaveAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistSelectAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistUnSelectAction;
import jp.biz.prominent.app.gui.AutoTwist.TwistUpdateAction;
import jp.biz.prominent.app.gui.IdModify.IdModifyPanel;
import jp.biz.prominent.app.gui.IdModify.ModifyAllSaveAction;
import jp.biz.prominent.app.gui.IdModify.ModifyCancelAction;
import jp.biz.prominent.app.gui.IdModify.ModifyDeleteAction;
import jp.biz.prominent.app.gui.IdModify.ModifyReferAction;
import jp.biz.prominent.app.gui.IdModify.ModifySaveAction;
import jp.biz.prominent.app.gui.IdRegist.IdRegistPanel;
import jp.biz.prominent.app.gui.IdRegist.RegistCancelAction;
import jp.biz.prominent.app.gui.IdRegist.RegistConfirmAction;
import jp.biz.prominent.app.gui.IdRegist.RegistLinkAction;
import jp.biz.prominent.app.gui.IdRegist.RegistSaveAction;
import lombok.Cleanup;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

@SuppressWarnings("serial")
@CommonsLog
public class BodyPanel extends JPanel {

	private JScrollPane pnlUserList;
	private JScrollPane pnlLogList;
	private ImageIcon tabIcon;
	public IdRegistPanel pnlIdRegist;
	public IdModifyPanel pnlIdModify;
	public AutoFollowPanel pnlAutoFollow;
	public AutoTwistPanel pnlAutoTwist;
	public JTabbedPane pnlFuncTab;
	public JTable tblUserList;
	public JTable tblLogList;
	public List<UserInfo> userInfoList;
	public int tabIndex;
	public boolean innerRowChange;
	public MainFrame mainFrame;
	public UserInfo userInfo;
	private DefaultTableModel userTableModel;
	private DefaultTableModel logTableModel;
	public int userIndex;

	private static final File LOG_DIR = new File("log");

	private static final String LIVE_LOG_NAME = "liveLogFile.log";
	private static final String BACK_LOG_NAME = "backLogFile.log";

	private static File backLog = new File(LOG_DIR, LIVE_LOG_NAME);
	private static File liveLog = new File(LOG_DIR, BACK_LOG_NAME);

	/**
	 * コンストラクタ
	 */
	public BodyPanel(MainFrame parent) {
		log.info("Body部初期化。");

		this.mainFrame = parent;
		this.setBackground(Constants.BG_COLOR);

		// IDリスト部分
		pnlUserList = new JScrollPane();
		pnlUserList.setBackground(Constants.BG_COLOR);
		pnlUserList.getViewport().setBackground(Constants.BG_COLOR);
		pnlUserList.setBounds(10, 5, 400, 405);
		pnlUserList.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " ユーザ一覧 ", TitledBorder.LEFT, TitledBorder.TOP));
		tblUserList = new JTable() {
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
		userTableModel = new DefaultTableModel(new String[] { "ツイッターＩＤ", "ニックネーム", "アイコン" }, 0) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Object.class, Object.class, Object.class };

			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tblUserList.setModel(userTableModel);
		tblUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblUserList.getColumnModel().getColumn(0).setResizable(false);
		tblUserList.getColumnModel().getColumn(0).setPreferredWidth(100);
		tblUserList.getColumnModel().getColumn(0).setMinWidth(100);
		tblUserList.getColumnModel().getColumn(0).setMaxWidth(100);
		tblUserList.getColumnModel().getColumn(1).setResizable(false);
		tblUserList.getColumnModel().getColumn(1).setPreferredWidth(200);
		tblUserList.getColumnModel().getColumn(1).setMinWidth(150);
		tblUserList.getColumnModel().getColumn(1).setMaxWidth(300);
		tblUserList.getColumnModel().getColumn(2).setResizable(false);
		tblUserList.getColumnModel().getColumn(2).setPreferredWidth(50);
		tblUserList.getColumnModel().getColumn(2).setMinWidth(50);
		tblUserList.getColumnModel().getColumn(2).setMaxWidth(50);
		tblUserList.setRowHeight(35);
		tblUserList.setBounds(12, 40, 400, 390);
		tblUserList.setSelectionBackground(Color.ORANGE);
		pnlUserList.setViewportView(tblUserList);
		this.add(pnlUserList);

		// Logリスト部分
		pnlLogList = new JScrollPane();
		pnlLogList.setBounds(10, 410, 400, 240);
		pnlLogList.setBackground(Constants.BG_COLOR);
		pnlLogList.getViewport().setBackground(Constants.BG_COLOR);
		pnlLogList.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " 自動処理ログ ", TitledBorder.LEFT, TitledBorder.TOP));
		tblLogList = new JTable() {
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
				if (c instanceof JComponent) {
					int mr = convertRowIndexToModel(row);
					int mc = convertColumnIndexToModel(column);
					Object o = getModel().getValueAt(mr, mc);
					String s = (o != null) ? o.toString() : null;
					((JComponent) c).setToolTipText(s.isEmpty() ? null : s);
				}
				return c;
			}
		};
		tblLogList.setRowSelectionAllowed(false);
		logTableModel = new DefaultTableModel(new Object[] { "自動処理ログ" }, 0) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Object.class };

			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tblLogList.setModel(logTableModel);
		tblLogList.getColumnModel().getColumn(0).setPreferredWidth(200);
		tblLogList.getColumnModel().getColumn(0).setMinWidth(200);
		tblLogList.getColumnModel().getColumn(0).setMaxWidth(5000);
		tblLogList.setBounds(12, 20, 400, 210);
		pnlLogList.setViewportView(tblLogList);
		this.add(pnlLogList);

		// タブ部分
		tabIcon = new ImageIcon("icon/Twitter_icon.png");
		pnlIdRegist = new IdRegistPanel();
		pnlIdModify = new IdModifyPanel(mainFrame);
		pnlAutoFollow = new AutoFollowPanel();
		pnlAutoTwist = new AutoTwistPanel();
		pnlFuncTab = new JTabbedPane();
		pnlFuncTab.setBounds(420, 8, 590, 640);
		pnlFuncTab.setBackground(Constants.BG_COLOR);
		pnlFuncTab.addTab("ユーザ登録", tabIcon, pnlIdRegist);
		pnlFuncTab.addTab("ユーザ修正", tabIcon, pnlIdModify);
		pnlFuncTab.addTab("自動フォロー設定", tabIcon, pnlAutoFollow);
		pnlFuncTab.addTab("自動ツィート設定", tabIcon, pnlAutoTwist);
		this.tabIndex = 0;
		this.add(pnlFuncTab);

		this.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.setLayout(null);
		this.setSize(1024, 690);

		userInfoList = new ArrayList<UserInfo>();

		// 初期設定:イベント
		this.initListener();

		// 初期設定:データ
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// 初期設定:データ
				initData();
			}
		});
	}

	/**
	 * 初期設定：イベント
	 */
	private void initListener() {

		// ---------------------------------------
		// -------------- BodyPanel --------------
		// ---------------------------------------
		// 行選択イベント
		tblUserList.addPropertyChangeListener("RowSelect", new UserRowSelectAtion(this));
		tblUserList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// イベントが重複処理を起こさないように
				if (e.getValueIsAdjusting()) {
					return;
				}
				if (innerRowChange == false) {
					tblUserList.firePropertyChange("RowSelect", -1, tblUserList.getSelectedRow());
				}
				innerRowChange = false;
			}
		});

		// タブ移動イベント
		// pnlFuncTab.addChangeListener(new TabChangeAction(this));

		// ---------------------------------------
		// -------------- IdRegistPanel ----------
		// ---------------------------------------
		// 申請リンク
		pnlIdRegist.lbltwitterApps.addMouseListener(new RegistLinkAction());

		// 確認ボタン
		pnlIdRegist.btnConfirm.addActionListener(new RegistConfirmAction(mainFrame));

		// 保存ボタン
		pnlIdRegist.btnSave.addActionListener(new RegistSaveAction(mainFrame, tblUserList, userInfoList));

		// 取消ボタン
		pnlIdRegist.btnCancel.addActionListener(new RegistCancelAction());

		// ---------------------------------------
		// -------------- IdModifyPanel ----------
		// ---------------------------------------
		// 参照ボタン
		pnlIdModify.btnIcon.addActionListener(new ModifyReferAction());

		// 全保存ボタン
		pnlIdModify.btnAllSave.addActionListener(new ModifyAllSaveAction(mainFrame, tblUserList, userInfoList));

		// 保存ボタン
		pnlIdModify.btnSave.addActionListener(new ModifySaveAction(mainFrame, tblUserList, userInfoList));

		// 削除ボタン
		pnlIdModify.btnDelete.addActionListener(new ModifyDeleteAction(mainFrame, tblUserList, userInfoList));

		// 取消ボタン
		pnlIdModify.btnCancel.addActionListener(new ModifyCancelAction());

		// ---------------------------------------
		// -------------- AutoFollowPanel --------
		// ---------------------------------------
		// 検索ボタン
		pnlAutoFollow.btnSearch.addActionListener(new FollowSearchAction(mainFrame));

		// アンフォローボタン
		pnlAutoFollow.btnUnFollow.addActionListener(new FollowUnFollowAction(mainFrame, tblUserList, userInfoList));

		// 再フォロー禁止リスト削除ボタン
		pnlAutoFollow.btnDelete.addActionListener(new FollowDeleteAction(mainFrame));

		// 保存ボタン
		pnlAutoFollow.btnSave.addActionListener(new FollowSaveAction(mainFrame, tblUserList, userInfoList));

		// 取消ボタン
		pnlAutoFollow.btnCancel.addActionListener(new FollowCancelAction());

		// ---------------------------------------
		// -------------- AutoTwistPanel -------
		// ---------------------------------------
		// 全選択ボタン
		pnlAutoTwist.btnSelAll.addActionListener(new TwistSelectAction());

		// 全解除ボタン
		pnlAutoTwist.btnUnSelAll.addActionListener(new TwistUnSelectAction());

		// 選択削除ボタン
		pnlAutoTwist.btnSelDel.addActionListener(new TwistDeleteAction(mainFrame, tblUserList, userInfoList));

		// 選択修正ボタン
		pnlAutoTwist.btnUpdate.addActionListener(new TwistUpdateAction(mainFrame, tblUserList, userInfoList));

		// 新規追加ボタン
		pnlAutoTwist.btnAdd.addActionListener(new TwistAddAction(mainFrame, tblUserList, userInfoList));

		// 保存ボタン
		pnlAutoTwist.btnSave.addActionListener(new TwistSaveAction(mainFrame, tblUserList, userInfoList));

		// 取消ボタン
		pnlAutoTwist.btnCancel.addActionListener(new TwistCancelAction());

		// テンプレート選択
		pnlAutoTwist.tblTemp.getSelectionModel().addListSelectionListener(new TwistRowSelectAtion(pnlAutoTwist));

	}

	/**
	 * 初期設定：データ
	 */
	private void initData() {
		log.info("初期データ設定。");

		// ---------------------------------------
		// -------------- BodyPanel --------------
		// ---------------------------------------
		// ユーザ情報リスト取得
		this.userInfoList.addAll(UserUtil.getAllUser());

		// GUIに追加
		DefaultTableModel tableModel = (DefaultTableModel) tblUserList.getModel();
		for (UserInfo userInfo : userInfoList) {
			// ユーザ認証
			userInfo.verify(mainFrame);
			tableModel.addRow(new Object[] { userInfo.getUserEntity().getUid(), userInfo.getUserEntity().getName(), CommonUtil.getImgTxt2(userInfo.getUserEntity().getImageUrl(), 35, 35) });
		}

		// 1件目ユーザの選択
		if (userInfoList.size() > 0) {
			// ユーザ登録画面
			pnlIdRegist.resetData();

			// ユーザ修正画面
			pnlIdModify.setUserData(userInfoList.get(0));
			pnlIdModify.backupData();

			// 自動フォロー画面
			pnlAutoFollow.setUserData(userInfoList.get(0));
			pnlAutoFollow.backupData();

			// 自動ツィート画面
			pnlAutoTwist.setUserData(userInfoList.get(0));
			pnlAutoTwist.backupData();

			mainFrame.bodyPanel.tblUserList.firePropertyChange("RowSelect", -1, 0);
		}
		// ユーザ未登録の場合
		else {
			mainFrame.headPanel.btnAutoFollow.setEnabled(false);
			mainFrame.headPanel.btnAutoTwitter.setEnabled(false);
			pnlFuncTab.setEnabledAt(1, false);
			pnlFuncTab.setEnabledAt(2, false);
			pnlFuncTab.setEnabledAt(3, false);
			JOptionPane.showMessageDialog(mainFrame, "ユーザを新規登録してください。", "情報", JOptionPane.INFORMATION_MESSAGE);
		}

		// 前回ログ
		this.readGuiLog();
	}

	/**
	 * 前回のGUIログの読出
	 */
	public void readGuiLog() {

		try {
			if (liveLog.exists() == false) {
				liveLog.createNewFile();
			}

			if (backLog.exists()) {
				LineIterator ite = FileUtils.lineIterator(backLog);

				while (ite.hasNext()) {
					logTableModel.addRow(new Object[] { ite.nextLine() });
					Rectangle rect = tblLogList.getCellRect(logTableModel.getRowCount() - 1, 0, true);
					tblLogList.scrollRectToVisible(rect);
				}

				backLog.delete();
			}

			liveLog.renameTo(new File(LOG_DIR, BACK_LOG_NAME));
			liveLog = new File(LOG_DIR, LIVE_LOG_NAME);
			liveLog.createNewFile();
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * GUIログの保存
	 *
	 * @param logValue ログ出力値 
	 */
	public void saveGuiLog(String logValue) {
		try {
			@Cleanup
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(liveLog, true)));
			pw.println(logValue);
		}
		catch (Exception e) {
			log.error(e);
		}
	}
}
