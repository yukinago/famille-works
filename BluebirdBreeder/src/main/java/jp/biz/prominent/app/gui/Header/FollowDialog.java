package jp.biz.prominent.app.gui.Header;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import lombok.extern.apachecommons.CommonsLog;

@SuppressWarnings("serial")
@CommonsLog
public class FollowDialog extends JDialog {

	private JPanel jPanel1;
	public JProgressBar progressBar;
	public JLabel titleLabel;
	public JButton stopBtn;
	public boolean stopFlg;

	public FollowDialog(Frame parent, boolean modal) {
		super(parent, modal);
		log.info("【自動フォロー】自動フォロー中...");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle("自動フォロー中...");
		setSize(300, 130);
		setResizable(false);

		jPanel1 = new JPanel();
		jPanel1.setLayout(null);

		titleLabel = new JLabel();
		titleLabel.setBounds(10, 10, 280, 20);
		jPanel1.add(titleLabel);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 40, 275, 20);
		jPanel1.add(progressBar);

		stopBtn = new JButton("自動フォロー停止");
		stopBtn.setBounds(80, 70, 140, 20);
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopFlg = true;
			}
		});
		jPanel1.add(stopBtn);

		getContentPane().add(jPanel1, BorderLayout.CENTER);
		stopFlg = false;
	}
}
