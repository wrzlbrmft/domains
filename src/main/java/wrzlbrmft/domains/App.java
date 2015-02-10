package wrzlbrmft.domains;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

public final class App implements Runnable {
	private static App instance;

	protected String domainsFileName;
	protected DomainList domains;

	private App() {}

	public static synchronized App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	public String getDomainsFileName() {
		return domainsFileName;
	}

	public void setDomainsFileName(String domainsFileName) {
		this.domainsFileName = domainsFileName;
	}

	public DomainList getDomains() {
		return domains;
	}

	public void setDomains(DomainList domains) {
		this.domains = domains;
	}

	public static Options getOptions() {
		Options options = Main.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("domains")
				.withDescription("text file containing list of domains")
				.hasArg()
				.withArgName("file")
				.create("d")
		);

		return options;
	}

	public static void optionDomainsFileName() {
		if (null != Main.getCommandLine() && Main.getCommandLine().hasOption("domains")) {
			App.getInstance().setDomainsFileName(Main.getCommandLine().getOptionValue("domains"));
		}
	}

	@Override
	public void run() {
		if (StringUtils.isNotBlank(getDomainsFileName())) {
			setDomains(new DomainList(getDomainsFileName()));

			for (Domain domain : getDomains()) {
				System.out.println(domain);
			}
		}
	}
}
