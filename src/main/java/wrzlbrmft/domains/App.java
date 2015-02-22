package wrzlbrmft.domains;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.util.Iterator;
import java.util.SortedSet;

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
				.withDescription("load domain list from text file")
				.hasArg()
				.withArgName("file")
				.create("d")
		);

		options.addOption(OptionBuilder
				.withLongOpt("exceptions")
				.withDescription("load exception list from text file")
				.hasArg()
				.withArgName("file")
				.create("e")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-redundant")
				.withDescription("remove redundant list entries (e.g. \".com\" includes \".foo.com\", so \".foo.com\" is redundant and removed)")
				.create("r")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-obsolete-domains")
				.withDescription("remove obsolete domain list entries (e.g. if \".com\" is on the exception list, the domain list entry \".foo.com\" is obsolete and removed)")
				.create("o")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-unused-exceptions")
				.withDescription("remove unused exception list entries (e.g. if \".com\" is NOT on the domain list, the exception list entry \".foo.com\" is unused and removed)")
				.create("u")
		);

		options.addOption(OptionBuilder
				.withLongOpt("save-domains")
				.withDescription("save optimized domain list as new text file")
				.hasArg()
				.withArgName("file")
				.create("s")
		);

		options.addOption(OptionBuilder
				.withLongOpt("save-exceptions")
				.withDescription("save optimized exception list as new text file")
				.hasArg()
				.withArgName("file")
				.create("x")
		);

		options.addOption(OptionBuilder
				.withLongOpt("verbose")
				.withDescription("be more verbose")
				.create("v")
		);

		return options;
	}

	public boolean loadDomains() {
		if (Main.getCommandLine().hasOption("domains")) {
			System.out.println("*** load, sort and de-duplicate domain list");
			String domainsFileName = Main.getCommandLine().getOptionValue("domains");
			setDomains(new DomainList(domainsFileName));
			return true;
		}
		return false;
	}

	public boolean saveDomains() {
		if (Main.getCommandLine().hasOption("save-domains")) {
			System.out.println("*** save optimized domain list");
			String saveDomainsFileName = Main.getCommandLine().getOptionValue("save-domains");
			return getDomains().save(saveDomainsFileName);
		}
		return false;
	}

	public boolean loadExceptions() {
		if (Main.getCommandLine().hasOption("exceptions")) {
			System.out.println("*** load, sort and de-duplicate exception list");
			String exceptionsFileName = Main.getCommandLine().getOptionValue("exceptions");
			setExceptions(new DomainList(exceptionsFileName));
			return true;
		}
		return false;
	}

	public boolean saveExceptions() {
		if (Main.getCommandLine().hasOption("save-exceptions")) {
			System.out.println("*** save optimized exception list");
			String saveExceptionsFileName = Main.getCommandLine().getOptionValue("save-exceptions");
			return getExceptions().save(saveExceptionsFileName);
		}
		return false;
	}

	@Override
	public void run() {
		if (loadDomains()) {
			System.out.println(String.format(
					">>> %d domain(s)",
					getDomains().size()
			));

			if (Main.getCommandLine().hasOption("remove-redundant")) {
				System.out.println("*** remove redundant domain list entries");
				DomainList redundantDomains = new DomainList(getDomains().removeRedundant());

				System.out.println(String.format(
						">>> %d domain(s) (removed %d redundant domain(s))",
						getDomains().size(),
						redundantDomains.size()
				));
			}
		}

		if (loadExceptions()) {
			System.out.println(String.format(
					">>> %d exception(s)",
					getExceptions().size()
			));

			if (Main.getCommandLine().hasOption("remove-redundant")) {
				System.out.println("*** remove redundant exception list entries");
				DomainList redundantExceptions = new DomainList(getExceptions().removeRedundant());

				System.out.println(String.format(
						">>> %d exception(s) (removed %d redundant exception(s))",
						getExceptions().size(),
						redundantExceptions.size()
				));
			}
		}

		if (null != getDomains() && null != getExceptions()) {
			Iterator<Domain> exceptionsIterator = getExceptions().iterator();
			while (exceptionsIterator.hasNext()) {
				Domain exception = exceptionsIterator.next();

				SortedSet<Domain> obsoleteDomains = getDomains().findChildrenOf(exception);

				if (0 < obsoleteDomains.size()) {
					if (Main.getCommandLine().hasOption("remove-obsolete-domains")) {
						getDomains().remove(obsoleteDomains);
					}
				}

				if (0 == obsoleteDomains.size() || Main.getCommandLine().hasOption("remove-obsolete-domains")) {
					Domain parent = exception.findParentIn(getDomains().getDomains());
					if (null == parent) {
						if (Main.getCommandLine().hasOption("remove-unused-exceptions")) {
							exceptionsIterator.remove();
						}
					}
				}
			}

			System.out.println("optimized domains = " + getDomains());
			System.out.println("optimized exceptions = " + getExceptions());
		}

		saveDomains();
		saveExceptions();
	}
}
