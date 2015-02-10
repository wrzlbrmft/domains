package wrzlbrmft.domains;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public final class App implements Runnable {
	private static App instance;

	protected DomainList domains = null;

	private App() {}

	public static synchronized App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
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

		options.addOption(OptionBuilder
				.withLongOpt("remove-redundant")
				.withDescription("remove redundant list entries (e.g. \".com\" includes \".foobar.com\", so \".foobar.com\" is removed)")
				.create("r")
		);

		return options;
	}

	@Override
	public void run() {
		if (null != Main.getCommandLine() && Main.getCommandLine().hasOption("domains")) {
			String domainsFileName = Main.getCommandLine().getOptionValue("domains");
			setDomains(new DomainList(domainsFileName));
		}

		if (null != getDomains()) {
			if (null != Main.getCommandLine() && Main.getCommandLine().hasOption("remove-redundant")) {
				DomainList redundantDomains = new DomainList(getDomains().removeRedundant());

				System.out.println("redundant domains = " + redundantDomains);
				System.out.println("unique domains = " + getDomains());
			}
		}
	}
}
