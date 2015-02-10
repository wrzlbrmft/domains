package wrzlbrmft.domains;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public final class App implements Runnable {
	private static App instance;

	protected String listFileName;
	protected DomainList list;

	private App() {}

	public static synchronized App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	public String getListFileName() {
		return listFileName;
	}

	public void setListFileName(String listFileName) {
		this.listFileName = listFileName;
	}

	public DomainList getList() {
		return list;
	}

	public void setList(DomainList list) {
		this.list = list;
	}

	public static Options getOptions() {
		Options options = Main.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("list")
				.withDescription("text file containing list of domains")
				.hasArg()
				.withArgName("file")
				.create("l")
		);

		return options;
	}

	public static void optionListFileName() {
		if (null != Main.getCommandLine() && Main.getCommandLine().hasOption("list")) {
			App.getInstance().setListFileName(Main.getCommandLine().getOptionValue("list"));
		}
	}

	@Override
	public void run() {
		setList(new DomainList(getListFileName()));

		for (Domain domain : list) {
			System.out.println(domain);
		}
	}
}
