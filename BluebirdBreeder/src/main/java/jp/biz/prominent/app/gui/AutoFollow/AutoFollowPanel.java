package jp.biz.prominent.app.gui.AutoFollow;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.ToolInformation;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.gui.PopupMenuMouseListener;
import jp.biz.prominent.app.entity.FollowerEntity;
import jp.biz.prominent.app.entity.UserEntity;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class AutoFollowPanel extends JPanel {

	public UserInfo userInfo;

	private JPanel pnlSearchKey;
	private JLabel lblSearchKey;
	private JLabel lblScope;
	private JPanel pnlGpsKey;
	private JLabel lblLongitude;
	private JLabel lblLatitude;
	private DefaultTableModel tableModel;
	private JLabel lblDelCond;
	private JLabel lblDelDays;
	private JLabel lblDelWeeks;
	private JLabel lblFollowCond;
	private JLabel lblFollowMax;

	public JLabel lblHitInfo;
	public JTextField txtKey1;
	public JTextField txtKey2;
	public JTextField txtKey3;
	public JTextField txtRadius;
	public JTextField txtLongitude;
	public JTextField txtLatitude;
	public JRadioButton rdbtnFollow;
	public JRadioButton rdbtnForbi;
	public JRadioButton rdbtnAnd;
	public JRadioButton rdbtnKey;
	public ButtonGroup bgSearchTp;
	public JCheckBox cbxFollow;
	public JCheckBox cbxReFollow;
	public JCheckBox cbxDuplicate;
	public JButton btnSearch;
	public JButton btnUnFollow;
	public JButton btnDelete;
	public JButton btnSave;
	public JButton btnCancel;
	public JScrollPane pnlFollower;
	public JTable tblFollower;
	public JTextField txtDelDays;
	public JTextField txtDelWeeks;
	public JTextField txtFollowMax;

	private String backKey1 = "";
	private String backKey2 = "";
	private String backKey3 = "";
	private String backRadius = "";
	private String backLongitude = "";
	private String backLatitude = "";
	private boolean backFollow = false;
	private boolean backReFollow = false;
	private boolean backDuplicate = false;
	private boolean backRdFollow = false;
	private boolean backRdForbi = false;
	private boolean backKey = false;
	private boolean backAnd = false;
	private String backDelDays = "";
	private String backDelWeeks = "";

	public boolean unSaveFlg = false;
	public int searchType = 0;

	/**
	 * コンストラクタ
	 */
	public AutoFollowPanel() {
		log.info("【自動フォロー設定】初期処理。");

		setLayout(null);
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(10, 10, 580, 600);
		this.setBackground(Constants.BG_COLOR);

		// 任意検索
		pnlSearchKey = new JPanel();
		pnlSearchKey.setBounds(12, 10, 556, 50);
		pnlSearchKey.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " 任意で検索キーワード ", TitledBorder.LEFT, TitledBorder.TOP));
		pnlSearchKey.setLayout(null);
		pnlSearchKey.setBackground(Constants.BG_COLOR);

		lblSearchKey = new JLabel("検索キーワード ：");
		lblSearchKey.setBounds(30, 20, 110, 20);
		pnlSearchKey.add(lblSearchKey);

		txtKey1 = new JTextField();
		txtKey1.setBackground(SystemColor.info);
		txtKey1.setBounds(150, 20, 100, 20);
		txtKey1.setColumns(10);
		pnlSearchKey.add(txtKey1);

		txtKey2 = new JTextField();
		txtKey2.setColumns(10);
		txtKey2.setBounds(260, 20, 100, 20);
		pnlSearchKey.add(txtKey2);

		txtKey3 = new JTextField();
		txtKey3.setColumns(10);
		txtKey3.setBounds(370, 20, 100, 20);
		pnlSearchKey.add(txtKey3);
		add(pnlSearchKey);

		// GPS検索
		pnlGpsKey = new JPanel();
		pnlGpsKey.setLayout(null);
		pnlGpsKey.setBounds(12, 70, 556, 50);
		pnlGpsKey.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " GPSで検索キーワード ", TitledBorder.LEFT, TitledBorder.TOP));
		pnlGpsKey.setBackground(Constants.BG_COLOR);
		add(pnlGpsKey);

		lblScope = new JLabel("範囲（km）：");
		lblScope.setHorizontalAlignment(SwingConstants.LEFT);
		lblScope.setBounds(30, 20, 95, 20);
		pnlGpsKey.add(lblScope);

		txtRadius = new JTextField();
		txtRadius.setBackground(SystemColor.info);
		txtRadius.setColumns(10);
		txtRadius.setBounds(130, 20, 70, 20);
		txtRadius.setEditable(ToolInformation.isEdittable());
		pnlGpsKey.add(txtRadius);

		lblLongitude = new JLabel("経度：");
		lblLongitude.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLongitude.setBounds(210, 20, 60, 20);
		pnlGpsKey.add(lblLongitude);

		txtLongitude = new JTextField();
		txtLongitude.setBackground(SystemColor.info);
		txtLongitude.setColumns(10);
		txtLongitude.setBounds(280, 21, 60, 20);
		txtLongitude.setEditable(ToolInformation.isEdittable());
		pnlGpsKey.add(txtLongitude);

		lblLatitude = new JLabel("緯度：");
		lblLatitude.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLatitude.setBounds(340, 20, 60, 20);
		pnlGpsKey.add(lblLatitude);

		txtLatitude = new JTextField();
		txtLatitude.setBackground(SystemColor.info);
		txtLatitude.setColumns(10);
		txtLatitude.setBounds(410, 21, 60, 20);
		txtLatitude.setEditable(ToolInformation.isEdittable());
		pnlGpsKey.add(txtLatitude);

		// 検索条件
		rdbtnFollow = new JRadioButton("フォロー済");
		rdbtnFollow.setSelected(true);
		rdbtnFollow.setBounds(140, 130, 90, 21);
		rdbtnFollow.setBackground(Constants.BG_COLOR);
		add(rdbtnFollow);

		rdbtnForbi = new JRadioButton("禁止リスト");
		rdbtnForbi.setSelected(false);
		rdbtnForbi.setBounds(230, 130, 90, 21);
		rdbtnForbi.setBackground(Constants.BG_COLOR);
		add(rdbtnForbi);

		rdbtnKey = new JRadioButton("KEY検索");
		rdbtnKey.setBounds(320, 130, 80, 21);
		rdbtnKey.setBackground(Constants.BG_COLOR);
		rdbtnKey.setSelected(false);
		add(rdbtnKey);

		rdbtnAnd = new JRadioButton("AND検索");
		rdbtnAnd.setBounds(400, 130, 80, 21);
		rdbtnAnd.setSelected(false);
		rdbtnAnd.setBackground(Constants.BG_COLOR);
		rdbtnAnd.setEnabled(ToolInformation.isEdittable());
		add(rdbtnAnd);

		ButtonGroup bgSearchTp = new ButtonGroup();
		bgSearchTp.add(rdbtnFollow);
		bgSearchTp.add(rdbtnForbi);
		bgSearchTp.add(rdbtnKey);
		bgSearchTp.add(rdbtnAnd);

		btnSearch = new JButton("検索");
		btnSearch.setBounds(498, 130, 70, 20);
		add(btnSearch);

		btnUnFollow = new JButton("アンフォロー");
		btnUnFollow.setBounds(236, 450, 120, 20);
		add(btnUnFollow);

		btnDelete = new JButton("再フォロー禁止リスト削除");
		btnDelete.setBounds(368, 450, 200, 20);
		add(btnDelete);

		btnSave = new JButton("保存");
		btnSave.setBounds(200, 570, 70, 20);
		add(btnSave);

		btnCancel = new JButton("取消");
		btnCancel.setBounds(330, 570, 70, 20);
		add(btnCancel);

		// フォローリスト
		pnlFollower = new JScrollPane();
		pnlFollower.setBounds(12, 150, 556, 300);
		pnlFollower.setBorder(new TitledBorder(new LineBorder(Color.GRAY), " フォローユーザ一覧 ", TitledBorder.LEFT, TitledBorder.TOP));
		pnlFollower.setBackground(Constants.BG_COLOR);
		pnlFollower.getViewport().setBackground(Constants.BG_COLOR);
		tblFollower = new JTable() {
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
		tableModel = new DefaultTableModel(new String[] { "アンフォロー", "状態", "ツイッターＩＤ", "ニックネーム", "アイコン" }, 0) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { Boolean.class, Object.class, Object.class, Object.class, Object.class };

			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { true, false, false, false, false };

			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		tblFollower.setModel(tableModel);
		tblFollower.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblFollower.getColumnModel().getColumn(0).setResizable(false);
		tblFollower.getColumnModel().getColumn(0).setPreferredWidth(80);
		tblFollower.getColumnModel().getColumn(0).setMinWidth(50);
		tblFollower.getColumnModel().getColumn(0).setMaxWidth(100);
		tblFollower.getColumnModel().getColumn(1).setResizable(false);
		tblFollower.getColumnModel().getColumn(1).setPreferredWidth(80);
		tblFollower.getColumnModel().getColumn(1).setMinWidth(45);
		tblFollower.getColumnModel().getColumn(1).setMaxWidth(90);
		tblFollower.getColumnModel().getColumn(2).setResizable(false);
		tblFollower.getColumnModel().getColumn(2).setPreferredWidth(100);
		tblFollower.getColumnModel().getColumn(2).setMinWidth(100);
		tblFollower.getColumnModel().getColumn(2).setMaxWidth(100);
		tblFollower.getColumnModel().getColumn(3).setResizable(false);
		tblFollower.getColumnModel().getColumn(3).setPreferredWidth(250);
		tblFollower.getColumnModel().getColumn(3).setMinWidth(80);
		tblFollower.getColumnModel().getColumn(3).setMaxWidth(800);
		tblFollower.getColumnModel().getColumn(4).setResizable(false);
		tblFollower.getColumnModel().getColumn(4).setPreferredWidth(50);
		tblFollower.getColumnModel().getColumn(4).setMinWidth(50);
		tblFollower.getColumnModel().getColumn(4).setMaxWidth(50);
		tblFollower.setRowHeight(35);
		tblFollower.setBounds(12, 44, 500, 485);
		tblFollower.setSelectionBackground(Color.ORANGE);
		pnlFollower.setViewportView(tblFollower);
		add(pnlFollower);

		lblHitInfo = new JLabel("");
		lblHitInfo.setHorizontalAlignment(SwingConstants.LEFT);
		lblHitInfo.setBounds(12, 450, 160, 20);
		add(lblHitInfo);

		lblDelCond = new JLabel("自動アンフォロー条件：");
		lblDelCond.setHorizontalAlignment(SwingConstants.LEFT);
		lblDelCond.setBounds(12, 480, 160, 20);
		add(lblDelCond);

		lblDelDays = new JLabel("①フォロワーとならなかった日数");
		lblDelDays.setHorizontalAlignment(SwingConstants.LEFT);
		lblDelDays.setBounds(184, 480, 204, 20);
		add(lblDelDays);

		lblDelWeeks = new JLabel("②ツィートの無い週数");
		lblDelWeeks.setHorizontalAlignment(SwingConstants.LEFT);
		lblDelWeeks.setBounds(184, 505, 183, 20);
		add(lblDelWeeks);

		txtDelDays = new JTextField();
		txtDelDays.setColumns(10);
		txtDelDays.setBackground(SystemColor.info);
		txtDelDays.setBounds(400, 480, 60, 20);
		add(txtDelDays);

		txtDelWeeks = new JTextField();
		txtDelWeeks.setColumns(10);
		txtDelWeeks.setBackground(SystemColor.info);
		txtDelWeeks.setBounds(400, 505, 60, 20);
		add(txtDelWeeks);

		lblFollowCond = new JLabel("フォロー条件：");
		lblFollowCond.setHorizontalAlignment(SwingConstants.LEFT);
		lblFollowCond.setBounds(12, 535, 100, 20);
		add(lblFollowCond);

		// フォロー条件
		cbxFollow = new JCheckBox("フォロー");
		cbxFollow.setBounds(120, 535, 80, 21);
		cbxFollow.setBackground(Constants.BG_COLOR);
		add(cbxFollow);

		cbxReFollow = new JCheckBox("フォロー返し");
		cbxReFollow.setBounds(200, 535, 110, 21);
		cbxReFollow.setBackground(Constants.BG_COLOR);
		cbxReFollow.setEnabled(ToolInformation.isEdittable());
		add(cbxReFollow);

		cbxDuplicate = new JCheckBox("重複可");
		cbxDuplicate.setBounds(310, 535, 70, 21);
		cbxDuplicate.setBackground(Constants.BG_COLOR);
		add(cbxDuplicate);

		lblFollowMax = new JLabel("１日のフォロー数：");
		lblFollowMax.setHorizontalAlignment(SwingConstants.LEFT);
		lblFollowMax.setBounds(380, 535, 130, 20);
		add(lblFollowMax);

		txtFollowMax = new JTextField();
		txtFollowMax.setColumns(4);
		txtFollowMax.setBackground(SystemColor.info);
		txtFollowMax.setBounds(510, 535, 40, 20);
		add(txtFollowMax);

		txtDelDays.addMouseListener(new PopupMenuMouseListener());
		txtDelWeeks.addMouseListener(new PopupMenuMouseListener());
		txtKey1.addMouseListener(new PopupMenuMouseListener());
		txtKey2.addMouseListener(new PopupMenuMouseListener());
		txtKey3.addMouseListener(new PopupMenuMouseListener());
		txtLatitude.addMouseListener(new PopupMenuMouseListener());
		txtLongitude.addMouseListener(new PopupMenuMouseListener());
		txtRadius.addMouseListener(new PopupMenuMouseListener());
	}

	/**
	 * 選択されたユーザ情報の表示
	 *
	 * @param userInfo
	 */
	public void setUserData(UserInfo userInfo) {

		this.userInfo = userInfo;
		UserEntity userEntity = userInfo.getUserEntity();

		// 検索条件
		this.txtKey1.setText(userEntity.getKeyword1());
		this.txtKey2.setText(userEntity.getKeyword2());
		this.txtKey3.setText(userEntity.getKeyword3());
		this.txtRadius.setText(userEntity.getDistance());
		this.txtLongitude.setText(userEntity.getLongitude());
		this.txtLatitude.setText(userEntity.getLatitude());

		// 検索対象
		switch (userEntity.getSearchType()) {
			case 1:
				this.rdbtnFollow.setSelected(true);
				this.rdbtnForbi.setSelected(false);
				this.rdbtnKey.setSelected(false);
				this.rdbtnAnd.setSelected(false);
				break;
			case 2:
				this.rdbtnFollow.setSelected(false);
				this.rdbtnForbi.setSelected(true);
				this.rdbtnKey.setSelected(false);
				this.rdbtnAnd.setSelected(false);
				break;
			case 3:
				this.rdbtnFollow.setSelected(false);
				this.rdbtnForbi.setSelected(false);
				this.rdbtnKey.setSelected(true);
				this.rdbtnAnd.setSelected(false);
				break;
			case 4:
				this.rdbtnFollow.setSelected(false);
				this.rdbtnForbi.setSelected(false);
				this.rdbtnKey.setSelected(false);
				this.rdbtnAnd.setSelected(true);
				break;
			default:
				break;
		}

		// フォロー条件
		this.cbxFollow.setSelected(userEntity.checkFollow());
		this.cbxReFollow.setSelected(userEntity.checkRefollow());
		this.cbxDuplicate.setSelected(userEntity.checkDuplicate());
		this.txtDelDays.setText(Integer.toString(userEntity.getThresholdNoFollow()));
		this.txtDelWeeks.setText(Integer.toString(userEntity.getThresholdNoTweet()));
		this.txtFollowMax.setText(Integer.toString(userEntity.getDailyFollowMax()));

		// 検索結果クリア
		tableModel.setRowCount(0);

		// フォローリスト
		List<FollowerEntity> followerEntityList = userEntity.getFollowerList();
		for (FollowerEntity followerEntity : followerEntityList) {
			// フォロー済
			if (userEntity.getSearchType() == 1 && (Constants.FOLLOW_LOCAL.equals(followerEntity.getStatus()) || Constants.FOLLOW_SERVER.equals(followerEntity.getStatus()))) {
				tableModel.addRow(new Object[] { followerEntity.checkUnfollow(), followerEntity.getStatus(), followerEntity.getFid(), followerEntity.getName(), followerEntity.getImageUrl() });
			}
			// 禁止リスト
			else if (userEntity.getSearchType() == 2 && Constants.FOLLOW_FORBI.equals(followerEntity.getStatus())) {
				tableModel.addRow(new Object[] { followerEntity.checkUnfollow(), followerEntity.getStatus(), followerEntity.getFid(), followerEntity.getName(), followerEntity.getImageUrl() });
			}
			// 検索結果
			else if ((userEntity.getSearchType() == 3 || userEntity.getSearchType() == 4) && Constants.FOLLOW_NOT.equals(followerEntity.getStatus())) {
				tableModel.addRow(new Object[] { followerEntity.checkUnfollow(), followerEntity.getStatus(), followerEntity.getFid(), followerEntity.getName(), followerEntity.getImageUrl() });
			}
		}

		// ヒット情報
		lblHitInfo.setText("ヒット件数：" + tableModel.getRowCount());

		unSaveFlg = false;

	}

	/**
	 * データ初期戻す
	 */
	public void resetData() {
		// 検索条件
		this.txtKey1.setText(backKey1);
		this.txtKey2.setText(backKey2);
		this.txtKey3.setText(backKey3);
		this.txtRadius.setText(backRadius);
		this.txtLongitude.setText(backLongitude);
		this.txtLatitude.setText(backLatitude);

		// 検索対象
		this.rdbtnFollow.setSelected(backRdFollow);
		this.rdbtnForbi.setSelected(backRdForbi);
		this.rdbtnKey.setSelected(backKey);
		this.rdbtnAnd.setSelected(backAnd);

		// フォロー条件
		this.cbxFollow.setSelected(backFollow);
		this.cbxReFollow.setSelected(backReFollow);
		this.cbxDuplicate.setSelected(backDuplicate);
		this.txtDelDays.setText(backDelDays);
		this.txtDelWeeks.setText(backDelWeeks);

		unSaveFlg = false;
	}

	/**
	 * 修正前のデータ保持
	 */
	public void backupData() {
		// 検索条件
		backKey1 = this.txtKey1.getText();
		backKey2 = this.txtKey2.getText();
		backKey3 = this.txtKey3.getText();
		backRadius = this.txtRadius.getText();
		backLongitude = this.txtLongitude.getText();
		backLatitude = this.txtLatitude.getText();

		// 検索対象
		backRdFollow = this.rdbtnFollow.isSelected();
		backRdForbi = this.rdbtnForbi.isSelected();
		backKey = this.rdbtnKey.isSelected();
		backAnd = this.rdbtnAnd.isSelected();

		// フォロー条件
		backFollow = this.cbxFollow.isSelected();
		backReFollow = this.cbxReFollow.isSelected();
		backDuplicate = this.cbxDuplicate.isSelected();
		backDelDays = this.txtDelDays.getText();
		backDelWeeks = this.txtDelWeeks.getText();
	}

	/**
	 * 未保存チェック
	 *
	 * @return
	 */
	public boolean hasUnSaved() {
		boolean hasUnsaved = false;

		hasUnsaved = hasUnsaved || unSaveFlg;

		// 検索条件
		if (backKey1.equals(txtKey1.getText()) == false) {
			hasUnsaved = true;
		}
		if (backKey2.equals(txtKey2.getText()) == false) {
			hasUnsaved = true;
		}
		if (backKey3.equals(txtKey3.getText()) == false) {
			hasUnsaved = true;
		}
		if (backRadius.equals(txtRadius.getText()) == false) {
			hasUnsaved = true;
		}
		if (backLongitude.equals(txtLongitude.getText()) == false) {
			hasUnsaved = true;
		}
		if (backLatitude.equals(txtLatitude.getText()) == false) {
			hasUnsaved = true;
		}

		// フォロー条件
		if (backFollow != this.cbxFollow.isSelected()) {
			hasUnsaved = true;
		}
		if (backReFollow != this.cbxReFollow.isSelected()) {
			hasUnsaved = true;
		}
		if (backDuplicate != this.cbxDuplicate.isSelected()) {
			hasUnsaved = true;
		}
		if (backDelDays.equals(txtDelDays.getText()) == false) {
			hasUnsaved = true;
		}
		if (backDelWeeks.equals(txtDelWeeks.getText()) == false) {
			hasUnsaved = true;
		}

		return hasUnsaved;
	}

	/**
	 * データ保存
	 */
	public void saveData() {
		UserEntity userEntity = userInfo.getUserEntity();

		// 検索条件
		userEntity.setKeyword1(this.txtKey1.getText());
		userEntity.setKeyword2(this.txtKey2.getText());
		userEntity.setKeyword3(this.txtKey3.getText());
		userEntity.setDistance(this.txtRadius.getText());
		userEntity.setLongitude(this.txtLongitude.getText());
		userEntity.setLatitude(this.txtLatitude.getText());

		// フォローリスト
		if (searchType == 3 || searchType == 4) {
			// 検索タイプ
			userEntity.setSearchType(searchType);

			// 新検索の未Follow対象を設定する
			List<FollowerEntity> followerEntityList = userEntity.getFollowerList();
			followerEntityList.clear();
			FollowerEntity followerEntity = null;
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				followerEntity = new FollowerEntity();
				followerEntity.setUid(userEntity.getUid());
				followerEntity.setUnfollow(((Boolean) tableModel.getValueAt(i, 0)) ? Constants.FLG_ON : Constants.FLG_ON);
				followerEntity.setStatus(tableModel.getValueAt(i, 1).toString());
				followerEntity.setFid(Long.valueOf(tableModel.getValueAt(i, 2).toString()));
				followerEntity.setName(tableModel.getValueAt(i, 3).toString());
				followerEntity.setImageUrl(tableModel.getValueAt(i, 4).toString());
				followerEntityList.add(followerEntity);
			}
		}

		// フォロー条件
		userEntity.setFollow(this.cbxFollow.isSelected() ? Constants.FLG_ON : Constants.FLG_OFF);
		userEntity.setRefollow(this.cbxReFollow.isSelected() ? Constants.FLG_ON : Constants.FLG_OFF);
		userEntity.setDuplicate(this.cbxDuplicate.isSelected() ? Constants.FLG_ON : Constants.FLG_OFF);
		userEntity.setThresholdNoFollow(Integer.parseInt(this.txtDelDays.getText()));
		userEntity.setThresholdNoTweet(Integer.parseInt(this.txtDelWeeks.getText()));
		userEntity.setDailyFollowMax(Integer.parseInt(this.txtFollowMax.getText()));
	}
}
