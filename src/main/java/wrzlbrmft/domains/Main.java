package wrzlbrmft.domains;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	private final static String PROPERTIES_ENTRY_FILE_NAME = "META-INF/main.properties";
	private final static Properties PROPERTIES = new Properties();

	protected static CommandLine commandLine;

	public static CommandLine getCommandLine() {
		return commandLine;
	}

	public static void setCommandLine(CommandLine commandLine) {
		Main.commandLine = commandLine;
	}

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

	public static File getFile() {
		return FileUtils.toFile(Main.class.getProtectionDomain().getCodeSource().getLocation());
	}

	public static String getFileName() {
		return getFile().getAbsolutePath();
	}

	public static Options getOptions() {
		Options options = new Options();

		options.addOption(OptionBuilder
				.withLongOpt("version")
				.withDescription("print version info and exit")
				.create("v")
		);

		options.addOption(OptionBuilder
				.withLongOpt("help")
				.withDescription("print this help message and exit")
				.create("h")
		);

		return options;
	}

	public static void parseCommandLine(String[] args) throws ParseException {
		CommandLineParser commandLineParser = new BasicParser();
		setCommandLine(commandLineParser.parse(App.getOptions(), args));
	}

	public static String getCommandLineSyntax() {
		return String.format(
				"java -jar %s",
				FilenameUtils.getName(getFileName())
		);
	}

	public static void printVersionInfo() {
		System.out.println(getVersionInfo());
	}

	public static void printHelpMessage() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(getCommandLineSyntax(), App.getOptions(), true);
	}

	public static void main(String[] args) {
		loadProperties();

		try {
			parseCommandLine(args);

			if (null != getCommandLine() && getCommandLine().hasOption("version")) {
				printVersionInfo();

				System.exit(0);
			}

			if (null != getCommandLine() && getCommandLine().hasOption("help")) {
				printHelpMessage();

				System.exit(0);
			}
		}
		catch (ParseException e) {
			System.out.println(e.getMessage());

			System.exit(1);
		}

		System.out.println(String.format(
				"starting %s",
				getVersionInfo()
		));
		App.getInstance().run();
	}
}
