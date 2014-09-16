package jp.biz.prominent.app.gui.Header;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import jp.biz.prominent.app.com.ToolInformation;
import jp.biz.prominent.app.com.UserInfo;
import jp.biz.prominent.app.gui.MainFrame;
import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class HeaderPanel extends JPanel {

	private JLabel lblNewLabel;
	public JButton btnAutoFollow;
	public JButton btnAutoTwitter;

	public JLabel runIcon;
	public JTextField runId;
	public MainFrame mainFrame;
	public UserInfo userInfo;
	public boolean runAutoFollow = false;
	public boolean runAutoTwist = false;

	public HeaderPanel(MainFrame parent) {
		log.info("Header部初期化。");

		this.mainFrame = parent;

		// ロゴ
		ImageIcon titleIcon = new ImageIcon("icon/Twitter_logo.png");
		lblNewLabel = new JLabel(ToolInformation.getTitle(), titleIcon, SwingConstants.LEFT);
		lblNewLabel.setBounds(12, 5, 450, 40);
		lblNewLabel.setFont(new Font("MS UI Gothic", Font.BOLD, 20));
		this.add(lblNewLabel);

		// 実行中ユーザアイコン
		runIcon = new JLabel("ICON");
		runIcon.setBorder(new LineBorder(new Color(0, 0, 0)));
		runIcon.setHorizontalAlignment(SwingConstants.CENTER);
		runIcon.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
		runIcon.setBackground(Color.LIGHT_GRAY);
		runIcon.setBounds(500, 5, 40, 40);
		this.add(runIcon);

		// フォロー開始ボタン
		ImageIcon followIcon1 = new ImageIcon("icon/AutoFollowStart1.gif");
		ImageIcon followIcon2 = new ImageIcon("icon/AutoFollowStart2.gif");
		btnAutoFollow = new JButton(followIcon1);
		btnAutoFollow.setRolloverIcon(followIcon2);
		btnAutoFollow.addActionListener(new AutoFollowAction(this.mainFrame));
		btnAutoFollow.setBounds(700, 10, 150, 30);
		btnAutoFollow.setBorder(null);
		this.add(btnAutoFollow);

		this.setLayout(null);
		this.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.setSize(1024, 50);

		runId = new JTextField();
		runId.setText("User ID");
		runId.setHorizontalAlignment(SwingConstants.CENTER);
		runId.setEditable(false);
		runId.setColumns(10);
		runId.setBounds(550, 10, 120, 30);
		add(runId);

		// ツイート開始ボタン
		ImageIcon twistIcon1 = new ImageIcon("icon/AutoTwistStart1.gif");
		ImageIcon twistIcon2 = new ImageIcon("icon/AutoTwistStart2.gif");
		btnAutoTwitter = new JButton(twistIcon1);
		btnAutoTwitter.setRolloverIcon(twistIcon2);
		btnAutoTwitter.addActionListener(new AutoTwistAction(this.mainFrame));
		btnAutoTwitter.setBounds(860, 10, 150, 30);
		btnAutoTwitter.setBorder(null);
		add(btnAutoTwitter);
	}
}
