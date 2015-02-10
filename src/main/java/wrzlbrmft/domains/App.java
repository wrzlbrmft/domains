package wrzlbrmft.domains;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public final class App implements Runnable {
	private static App instance;

	protected DomainList domains = null;
	protected DomainList exceptions = null;

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

	public DomainList getExceptions() {
		return exceptions;
	}

	public void setExceptions(DomainList exceptions) {
		this.exceptions = exceptions;
	}

	public static Options getOptions() {
		Options options = Main.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("domains")
				.withDescription("load domains list from text file")
				.hasArg()
				.withArgName("file")
				.create("d")
		);

		options.addOption(OptionBuilder
				.withLongOpt("exceptions")
				.withDescription("load exceptions list from text file")
				.hasArg()
				.withArgName("file")
				.create("e")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-redundant")
				.withDescription("remove redundant list entries (e.g. \".foo.com\" includes \".bar.foo.com\", so \".bar.foo.com\" is removed)")
				.create("r")
		);

		options.addOption(OptionBuilder
				.withLongOpt("save-domains")
				.withDescription("save optimized domains list to text file")
				.hasArg()
				.withArgName("file")
				.create()
		);

		options.addOption(OptionBuilder
				.withLongOpt("save-exceptions")
				.withDescription("save optimized exceptions list to text file")
				.hasArg()
				.withArgName("file")
				.create()
		);

		return options;
	}

	public boolean loadDomains() {
		if (Main.getCommandLine().hasOption("domains")) {
			String domainsFileName = Main.getCommandLine().getOptionValue("domains");
			setDomains(new DomainList(domainsFileName));
			return true;
		}
		return false;
	}

	public boolean saveDomains() {
		if (Main.getCommandLine().hasOption("save-domains")) {
			String saveDomainsFileName = Main.getCommandLine().getOptionValue("save-domains");
			return getDomains().save(saveDomainsFileName);
		}
		return false;
	}

	public boolean loadExceptions() {
		if (Main.getCommandLine().hasOption("exceptions")) {
			String exceptionsFileName = Main.getCommandLine().getOptionValue("exceptions");
			setExceptions(new DomainList(exceptionsFileName));
			return true;
		}
		return false;
	}

	public boolean saveExceptions() {
		if (Main.getCommandLine().hasOption("save-exceptions")) {
			String saveExceptionsFileName = Main.getCommandLine().getOptionValue("save-exceptions");
			return getExceptions().save(saveExceptionsFileName);
		}
		return false;
	}

	@Override
	public void run() {
		if (loadDomains()) {
			if (Main.getCommandLine().hasOption("remove-redundant")) {
				DomainList redundantDomains = new DomainList(getDomains().removeRedundant());

				System.out.println("redundant domains = " + redundantDomains);
				System.out.println("unique domains = " + getDomains());
			}
		}

		if (loadExceptions()) {
			if (Main.getCommandLine().hasOption("remove-redundant")) {
				DomainList redundantExceptions = new DomainList(getExceptions().removeRedundant());

				System.out.println("redundant exceptions = " + redundantExceptions);
				System.out.println("unique exceptions = " + getExceptions());
			}
		}

		saveDomains();
		saveExceptions();
	}
}
