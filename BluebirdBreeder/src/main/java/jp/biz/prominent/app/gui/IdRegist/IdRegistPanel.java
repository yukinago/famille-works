package jp.biz.prominent.app.gui.IdRegist;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.SystemColor;

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
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class IdRegistPanel extends JPanel {
	public UserInfo userInfo;

	private JLabel lblCmt;
	private JLabel lblConKey;
	private JLabel lblConSec;
	private JLabel lblAccTkn;
	private JLabel lblAccTknSec;
	private JLabel lblId;
	private JLabel lblName;
	public JLabel lbltwitterApps;
	public JLabel lblIconImg;
	public JTextField txtConKey;
	public JTextField txtConSec;
	public JTextField txtAccTkn;
	public JTextField txtAccTknSec;
	public JTextField txtId;
	public JTextField txtName;
	public JButton btnConfirm;
	public JButton btnSave;
	public JButton btnCancel;

	public String bkConKey = "";
	public String bkConSec = "";
	public String bkAccTkn = "";
	public String bkAccTknSec = "";

	/**
	 * コンストラクタ
	 */
	public IdRegistPanel() {
		log.info("【ユーザ登録】初期処理。");

		this.setLayout(null);
		this.setBounds(10, 10, 580, 600);
		this.setBackground(Constants.BG_COLOR);

		lblCmt = new JLabel("新規ツイッターユーザの登録を行います。");
		lblCmt.setHorizontalAlignment(SwingConstants.LEFT);
		lblCmt.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblCmt.setBounds(24, 20, 273, 15);
		this.add(lblCmt);

		lbltwitterApps = new JLabel("<html><body><a href=\"\">事前にtwitter.comよりアプリ申請を行ってください。</a></body></html>");
		lbltwitterApps.setHorizontalAlignment(SwingConstants.CENTER);
		lbltwitterApps.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lbltwitterApps.setBounds(230, 45, 250, 15);
		lbltwitterApps.setCursor(new Cursor(Cursor.HAND_CURSOR));
		add(lbltwitterApps);

		lblIconImg = new JLabel("ICON");
		lblIconImg.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblIconImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblIconImg.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblIconImg.setBackground(Color.LIGHT_GRAY);
		lblIconImg.setBounds(400, 210, 80, 80);
		this.add(lblIconImg);

		lblConKey = new JLabel("Consumer key ：");
		lblConKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConKey.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblConKey.setBounds(24, 70, 144, 20);
		this.add(lblConKey);

		txtConKey = new JTextField();
		txtConKey.setBackground(SystemColor.info);
		txtConKey.setColumns(10);
		txtConKey.setBounds(180, 70, 300, 20);
		this.add(txtConKey);

		lblConSec = new JLabel("Consumer secret ：");
		lblConSec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConSec.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblConSec.setBounds(24, 100, 144, 20);
		this.add(lblConSec);

		txtConSec = new JTextField();
		txtConSec.setBackground(SystemColor.info);
		txtConSec.setBounds(180, 100, 300, 20);
		this.add(txtConSec);

		lblAccTkn = new JLabel("Access token ：");
		lblAccTkn.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccTkn.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblAccTkn.setBounds(24, 130, 144, 20);
		add(lblAccTkn);

		txtAccTkn = new JTextField();
		txtAccTkn.setBackground(SystemColor.info);
		txtAccTkn.setBounds(180, 130, 300, 20);
		add(txtAccTkn);

		lblAccTknSec = new JLabel("Access token secret ：");
		lblAccTknSec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccTknSec.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblAccTknSec.setBounds(24, 160, 144, 20);
		add(lblAccTknSec);

		txtAccTknSec = new JTextField();
		txtAccTknSec.setBackground(SystemColor.info);
		txtAccTknSec.setBounds(180, 160, 300, 20);
		add(txtAccTknSec);

		lblId = new JLabel("ツイッターＩＤ ：");
		lblId.setHorizontalAlignment(SwingConstants.RIGHT);
		lblId.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblId.setBounds(24, 210, 144, 20);
		add(lblId);

		txtId = new JTextField();
		txtId.setEditable(false);
		txtId.setColumns(10);
		txtId.setBounds(180, 210, 200, 20);
		add(txtId);

		lblName = new JLabel("ニックネーム ：");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblName.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		lblName.setBounds(24, 240, 144, 20);
		this.add(lblName);

		txtName = new JTextField();
		txtName.setEditable(false);
		txtName.setColumns(10);
		txtName.setBounds(180, 240, 200, 20);
		this.add(txtName);

		btnConfirm = new JButton("確認");
		btnConfirm.setBounds(160, 330, 70, 20);
		add(btnConfirm);

		btnSave = new JButton("保存");
		btnSave.setEnabled(false);
		btnSave.setBounds(260, 330, 70, 20);
		this.add(btnSave);

		btnCancel = new JButton("取消");
		btnCancel.setBounds(360, 330, 70, 20);
		this.add(btnCancel);

		txtAccTkn.addMouseListener(new PopupMenuMouseListener());
		txtAccTknSec.addMouseListener(new PopupMenuMouseListener());
		txtConKey.addMouseListener(new PopupMenuMouseListener());
		txtConSec.addMouseListener(new PopupMenuMouseListener());
	}

	/**
	 * 情報の設定
	 */
	public void setData(UserInfo userInfo) {
		txtId.setText(String.valueOf(userInfo.getId()));
		txtName.setText(userInfo.getName());

		UserEntity userEntity = userInfo.getUserEntity();
		userEntity.setUid(userInfo.getId());
		userEntity.setName(userInfo.getName());
		lblIconImg.setText(CommonUtil.getImgTxt2(userEntity.getImageUrl(), 80, 80));

		this.userInfo = userInfo;
	}

	/**
	 * データ初期戻す
	 */
	public void resetData() {
		txtConKey.setText("");
		txtConSec.setText("");
		txtAccTkn.setText("");
		txtAccTknSec.setText("");
		txtId.setText("");
		txtName.setText("");
		lblIconImg.setText("ICON");
		txtConKey.setEnabled(true);
		txtConSec.setEnabled(true);
		txtAccTkn.setEnabled(true);
		txtAccTknSec.setEnabled(true);
		btnConfirm.setEnabled(true);
		btnSave.setEnabled(false);
	}

	/**
	 * 未保存チェック
	 *
	 * @return
	 */
	public boolean hasUnSaved() {
		boolean hasUnsaved = false;
		if ("".equals(txtConKey.getText()) == false) {
			hasUnsaved = true;
		}
		if ("".equals(txtConSec.getText()) == false) {
			hasUnsaved = true;
		}
		if ("".equals(txtAccTkn.getText()) == false) {
			hasUnsaved = true;
		}
		if ("".equals(txtAccTknSec.getText()) == false) {
			hasUnsaved = true;
		}

		return hasUnsaved;
	}
}
