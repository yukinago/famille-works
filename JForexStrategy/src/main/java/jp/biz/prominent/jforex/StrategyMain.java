package jp.biz.prominent.jforex;

import java.io.File;

import jp.biz.prominent.jforex.entity.StrategyConfig;
import lombok.extern.apachecommons.CommonsLog;

import org.apache.commons.io.FileUtils;

import com.dukascopy.api.Period;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;

@CommonsLog
public class StrategyMain {

	// url of the DEMO jnlp
	private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	// user name
	//	private static String userName = "Karman120xD";
	private static String userName = "Sudbrock70xD";

	// password
	//	private static String password = "oFqSm332";
	private static String password = "XCLbe211";

	public static void main(String[] args) throws Exception {
		// get the instance of the IClient interface
		final IClient client = ClientFactory.getDefaultInstance();
		// set the listener that will receive system events
		client.setSystemListener(new ISystemListener() {
			private int lightReconnects = 3;

			@Override
			public void onStart(long processId) {
				log.info("Strategy started: " + processId);
			}

			@Override
			public void onStop(long processId) {
				log.info("Strategy stopped: " + processId);
				if (client.getStartedStrategies().size() == 0) {
					System.exit(0);
				}
			}

			@Override
			public void onConnect() {
				log.info("Connected");
				lightReconnects = 3;
			}

			@Override
			public void onDisconnect() {
				log.warn("Disconnected");
				if (lightReconnects > 0) {
					client.reconnect();
					--lightReconnects;
				}
				else {
					try {
						// sleep for 10 seconds before attempting to reconnect
						Thread.sleep(10000);
					}
					catch (InterruptedException e) {
						// ignore
					}
					try {
						client.connect(jnlpUrl, userName, password);
					}
					catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		});

		log.info("Connecting...");
		// connect to the server using jnlp, user name and password
		client.connect(jnlpUrl, userName, password);

		int i = 10; // wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			log.error("Failed to connect Dukascopy servers");
			System.exit(1);
		}

		StrategyConfig config = new StrategyConfig();
		// config.setInstruments(Instrument.USDJPY.toString());
		config.setBarPeriods(Period.ONE_MIN.toString());

		// subscribe to the instruments
		log.info("Subscribing instruments...");
		client.setSubscribedInstruments(config.getInstrumentSet());

		// workaround for LoadNumberOfCandlesAction for JForex-API versions >
		// 2.6.64
		Thread.sleep(5000);

		// start the strategy
		log.info("Starting strategy");

//		BbandsAndFractalStorategy storategy = new BbandsAndFractalStorategy();
//		client.startStrategy(storategy);
		client.compileStrategy("/home/yukinago/NetBeansProjects/JForexStrategy/src/main/java/jp/biz/prominent/jforex/strategy/BbandsAndFractalStorategy.java",
				false);
		client.compileStrategy("/home/yukinago/NetBeansProjects/JForexStrategy/src/main/java/jp/biz/prominent/jforex/strategy/BbandsWatchTrendStorategy.java",
				false);
		client.disconnect();
		FileUtils.copyFile(
				new File("/home/yukinago/NetBeansProjects/JForexStrategy/src/main/java/jp/biz/prominent/jforex/strategy/BbandsAndFractalStorategy.jfx"),
				new File("/home/yukinago/Desktop/BbandsAndFractalStorategy.jfx")
				);
		FileUtils.copyFile(
				new File("/home/yukinago/NetBeansProjects/JForexStrategy/src/main/java/jp/biz/prominent/jforex/strategy/BbandsWatchTrendStorategy.jfx"),
				new File("/home/yukinago/Desktop/BbandsWatchTrendStorategy.jfx")
				);

		System.exit(0);

		// IIndicators indicators = storategy.getIndicators();
		//
		// JFrame frame = new JFrame("チャート");
		// frame.setLayout(null);
		// frame.setSize(800, 600);
		//
		// int x = 0;
		// int y = 0;
		//
		// for (Instrument instrument : config.getInstrumentSet()) {
		// IFeedDescriptor feedDescriptor = new
		// RangeBarFeedDescriptor(instrument, PriceRange.FIVE_PIPS,
		// OfferSide.BID, config.getPeriod());
		// feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
		//
		// IChart chart = client.openChart(feedDescriptor);
		//
		// chart.add(indicators.getIndicator("BBANDS"));
		// chart.add(indicators.getIndicator("FRACTAL"));
		//
		// final IClientGUI clientGUI = client.getClientGUI(chart);
		// JLabel label = new JLabel(instrument.toString(),
		// SwingConstants.LEFT);
		// label.setBounds(x, y, 390, 20);
		// JPanel panel = clientGUI.getChartPanel();
		// panel.setBounds(x, y + 20, 390, 240);
		//
		// if (x == 0) {
		// x += 400;
		// }
		// else {
		// x = 0;
		// y += 300;
		// }
		//
		// frame.add(label);
		// frame.add(panel);
		// }
		//
		// frame.setVisible(true);

		// now it's running
	}
}
