package jp.biz.prominent.app.com.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class ProgressDialog<T> extends JDialog {

	private Future<T> future;
	private ExecutorService executor;
	private ExecutorService observer;
	private JPanel jPanel1;
	private JProgressBar progressBar;
	private JLabel titleLabel;

	public ProgressDialog(Frame parent, boolean modal) {
		super(parent, modal);

		initComponents();

		executor = Executors.newSingleThreadExecutor();
		observer = Executors.newSingleThreadExecutor();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (future == null) {
					setVisible(false);
				}

				observer.submit(new Runnable() {
					public void run() {
						while (!future.isDone()) {
							try {
								Thread.sleep(1000);
							}
							catch (InterruptedException ex) {
								break;
							}
						}
						future = null;
						setVisible(false);
					}
				});
			}
		});
	}

	/**
	 * 初期化
	 */
	private void initComponents() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle("実行中...");
		setSize(300, 100);
		setResizable(false);

		jPanel1 = new JPanel();
		jPanel1.setLayout(new GridBagLayout());

		titleLabel = new JLabel("処理中です。しばらくお待ちください。");
		jPanel1.add(titleLabel, this.gridBagCons(0, 0, 1, 2));

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		jPanel1.add(progressBar, this.gridBagCons(0, 2, 1, 1));

		getContentPane().add(jPanel1, BorderLayout.CENTER);
	}

	/**
	 * レイアウトGrid
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private GridBagConstraints gridBagCons(int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		return gbc;
	}

	/**
	 * 処理開始
	 */
	public Future<T> submit(Callable<T> task) {
		if (future != null) {
			throw new IllegalStateException("Task already submitted.");
		}
		future = executor.submit(task);
		return future;
	}

	/**
	 * 処理中止
	 */
	public void cancel() {
		if (future != null && !future.isDone() && !future.isCancelled()) {
			future.cancel(true);
		}
		try {
			Thread.sleep(1000);
		}
		catch (Exception e) {
			//
		}
	}
}
