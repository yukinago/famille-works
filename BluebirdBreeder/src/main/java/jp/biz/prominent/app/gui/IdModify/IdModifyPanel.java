package jp.biz.prominent.app.gui.IdModify;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import jp.biz.prominent.app.com.CommonUtil;
import jp.biz.prominent.app.com.Constants;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.com.gui.PopupMenuMouseListener;
import jp.biz.prominent.app.entity.UserEntity;
import jp.biz.prominent.app.gui.MainFrame;
import jp.biz.prominent.app.gui.text.MaxSizeText;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class IdModifyPanel extends JPanel {
	public UserInfo userInfo;

	private JLabel lblCmt;
	private JLabel lblConKey;
	private JLabel lblConSec;
	private JLabel lblAccTkn;
	private JLabel lblAccTknSec;
	private JLabel lblId;
	private JLabel lblName;
	private JLabel lblIcon;
	private JLabel lblInfo;
	public JLabel lblIconImg;
	public JTextField txtConKey;
	public JTextField txtConSec;
	public JTextField txtAccTkn;
	public JTextField txtAccTknSec;
	public JTextField txtId;
	public JTextField txtName;
	public JTextField txtIcon;
	public JButton btnIcon;
	public JButton btnAllSave;
	public JButton btnSave;
	public JButton btnDelete;
	public JButton btnCancel;

	private String bkId = "";
	private String bkName = "";
	private String bkIcon = "";

	private MainFrame mainFrame;

	/**
	 * コンストラクタ
	 */
	public IdModifyPanel(MainFrame mainFrame) {
		log.info("【ユーザ修正】初期処理。");

		this.mainFrame = mainFrame;

		this.setLayout(null);
		this.setBounds(10, 10, 580, 600);
		this.setBackground(Constants.BG_COLOR);

		lblCmt = new JLabel("選択されたツイッターＩＤの修正を行います。");
		lblCmt.setHorizontalAlignment(SwingConstants.LEFT);
		lblCmt.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblCmt.setBounds(24, 20, 273, 15);
		this.add(lblCmt);

		lblConKey = new JLabel("Consumer key ：");
		lblConKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConKey.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblConKey.setBounds(24, 70, 144, 20);
		add(lblConKey);

		txtConKey = new JTextField();
		txtConKey.setHorizontalAlignment(SwingConstants.LEFT);
		txtConKey.setEditable(false);
		txtConKey.setColumns(10);
		txtConKey.setBounds(180, 70, 300, 20);
		add(txtConKey);

		lblConSec = new JLabel("Consumer secret ：");
		lblConSec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConSec.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblConSec.setBounds(24, 100, 144, 20);
		add(lblConSec);

		txtConSec = new JTextField();
		txtConSec.setHorizontalAlignment(SwingConstants.LEFT);
		txtConSec.setEditable(false);
		txtConSec.setBounds(180, 100, 300, 20);
		add(txtConSec);

		lblAccTkn = new JLabel("Access token ：");
		lblAccTkn.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccTkn.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblAccTkn.setBounds(24, 130, 144, 20);
		add(lblAccTkn);

		txtAccTkn = new JTextField();
		txtAccTkn.setHorizontalAlignment(SwingConstants.LEFT);
		txtAccTkn.setEditable(false);
		txtAccTkn.setBounds(180, 130, 300, 20);
		add(txtAccTkn);

		lblAccTknSec = new JLabel("Access token secret ：");
		lblAccTknSec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccTknSec.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblAccTknSec.setBounds(24, 160, 144, 20);
		add(lblAccTknSec);

		txtAccTknSec = new JTextField();
		txtAccTknSec.setHorizontalAlignment(SwingConstants.LEFT);
		txtAccTknSec.setEditable(false);
		txtAccTknSec.setBounds(180, 160, 300, 20);
		add(txtAccTknSec);

		lblId = new JLabel("ツイッターＩＤ ：");
		lblId.setHorizontalAlignment(SwingConstants.RIGHT);
		lblId.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblId.setBounds(24, 210, 144, 20);
		this.add(lblId);

		txtId = new JTextField();
		;
		txtId.setEditable(false);
		txtId.setColumns(10);
		txtId.setBounds(180, 210, 200, 20);
		this.add(txtId);

		lblName = new JLabel("ニックネーム ：");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblName.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblName.setBounds(24, 240, 144, 20);
		this.add(lblName);

		txtName = new MaxSizeText(20);
		txtName.setColumns(10);
		txtName.setBounds(180, 240, 200, 20);
		this.add(txtName);

		lblIcon = new JLabel("アイコン ：");
		lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIcon.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblIcon.setBounds(24, 300, 144, 20);
		add(lblIcon);

		txtIcon = new JTextField();
		txtIcon.setColumns(10);
		txtIcon.setBounds(180, 300, 200, 20);
		this.add(txtIcon);

		btnIcon = new JButton("参照");
		btnIcon.setBounds(410, 300, 70, 20);
		this.add(btnIcon);

		lblInfo = new JLabel("<html>画像は700KB以下でなくてはいけません。<br>対応フォーマットはGIF、JPEG、PNGです。</html>");
		lblInfo.setHorizontalAlignment(SwingConstants.LEFT);
		lblInfo.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblInfo.setBounds(200, 325, 220, 40);
		this.add(lblInfo);

		lblIconImg = new JLabel("ICON");
		lblIconImg.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblIconImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblIconImg.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblIconImg.setBackground(Color.LIGHT_GRAY);
		lblIconImg.setBounds(400, 210, 80, 80);
		this.add(lblIconImg);

		btnAllSave = new JButton("全保存");
		btnAllSave.setBounds(120, 400, 90, 20);
		this.add(btnAllSave);

		btnSave = new JButton("保存");
		btnSave.setBounds(230, 400, 70, 20);
		this.add(btnSave);

		btnDelete = new JButton("削除");
		btnDelete.setBounds(320, 400, 70, 20);
		this.add(btnDelete);

		btnCancel = new JButton("取消");
		btnCancel.setBounds(410, 400, 70, 20);
		this.add(btnCancel);

		JLabel label = new JLabel("20文字以内でなくてはいけません。");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		label.setBounds(200, 265, 180, 20);
		add(label);

		txtIcon.addMouseListener(new PopupMenuMouseListener());
		txtName.addMouseListener(new PopupMenuMouseListener());
	}

	/**
	 * 選択されたユーザ情報の表示
	 *
	 * @param userInfo
	 */
	public void setUserData(UserInfo userInfo) {
		this.userInfo = userInfo;

		UserEntity userEntity = userInfo.getUserEntity();
		if (userEntity != null) {
			this.txtConKey.setText(userEntity.getConsumerKey());
			this.txtConSec.setText(userEntity.getConsumerSecret());
			this.txtAccTkn.setText(userEntity.getAccessToken());
			this.txtAccTknSec.setText(userEntity.getAccessTokenSecret());
			this.txtId.setText(String.valueOf(userEntity.getUid()));
			this.txtName.setText(userEntity.getName());
			this.lblIconImg.setText(CommonUtil.getImgTxt2(userInfo.getUserEntity().getImageUrl(), 80, 80));
		}

		this.txtIcon.setText("");
	}

	/**
	 * データ初期戻す
	 */
	public void resetData() {
		this.txtId.setText(this.bkId);
		this.txtName.setText(this.bkName);
		this.txtIcon.setText(this.bkIcon);
	}

	/**
	 * 修正前のデータ保持
	 */
	public void backupData() {
		this.bkId = this.txtId.getText();
		this.bkName = this.txtName.getText();
		this.bkIcon = this.txtIcon.getText();
	}

	/**
	 * 未保存チェック
	 *
	 * @return
	 */
	public boolean hasUnSaved() {
		boolean hasUnsaved = false;

		if (bkName.equals(txtName.getText()) == false) {
			hasUnsaved = true;
		}
		if (bkIcon.equals(txtIcon.getText()) == false) {
			hasUnsaved = true;
		}

		return hasUnsaved;
	}

	/**
	 * データ保存
	 */
	public void saveData() {
		userInfo.updateUserData(txtIcon.getText(), txtName.getText(), mainFrame);
	}
}
