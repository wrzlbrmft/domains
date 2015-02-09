package wrzlbrmft.domains;

import org.apache.commons.cli.Options;

public final class App implements Runnable {
	private static App instance;

	private App() {}

	public static synchronized App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	public static Options getOptions() {
		Options options = Main.getOptions();

		return options;
	}

	@Override
	public void run() {
		System.out.println("Hello World!");
	}
}
