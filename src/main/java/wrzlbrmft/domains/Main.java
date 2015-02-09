package wrzlbrmft.domains;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	private final static String PROPERTIES_ENTRY_FILE_NAME = "META-INF/main.properties";
	private final static Properties PROPERTIES = new Properties();

	public static boolean loadProperties() {
		InputStream inputStream = null;
		try {
			inputStream = Main.class.getResourceAsStream("/" + PROPERTIES_ENTRY_FILE_NAME);
			PROPERTIES.load(inputStream);
			return true;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		finally {
			IOUtils.closeQuietly(inputStream);
		}
		return false;
	}

	public static String getAppName() {
		return PROPERTIES.getProperty("app.name");
	}

	public static String getAppVersion() {
		return PROPERTIES.getProperty("app.version");
	}

	public static String getVersionInfo() {
		return getAppName() + " " + getAppVersion();
	}

	public static void main(String[] args) {
		loadProperties();

		System.out.println(String.format(
				"starting %s",
				getVersionInfo()
		));
		App.getInstance().run();
	}
}
