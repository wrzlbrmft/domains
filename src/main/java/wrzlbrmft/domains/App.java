package wrzlbrmft.domains;

public final class App implements Runnable {
	private static App instance;

	private App() {}

	public static synchronized App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	@Override
	public void run() {
		System.out.println("Hello World!");
	}
}
