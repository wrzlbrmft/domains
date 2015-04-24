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
				.withDescription("remove redundant list entries (e.g. \"com\" includes \"foo.com\", so \"foo.com\" is redundant and removed)")
				.create("r")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-obsolete-domains")
				.withDescription("remove obsolete domain list entries (e.g. if \"com\" is on the exception list, the domain list entry \"foo.com\" is obsolete and removed)")
				.create("o")
		);

		options.addOption(OptionBuilder
				.withLongOpt("remove-unused-exceptions")
				.withDescription("remove unused exception list entries (e.g. if \"com\" is not on the domain list, the exception list entry \"foo.com\" is unused and removed)")
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
				.withLongOpt("check-whitelist")
				.withDescription("check a domain by treating the loaded domain(/exception) list as whitelist(s)")
				.hasArg()
				.withArgName("domain")
				.create("w")
		);

		options.addOption(OptionBuilder
				.withLongOpt("check-blacklist")
				.withDescription("check a domain by treating the loaded domain(/exception) list as blacklist(s)")
				.hasArg()
				.withArgName("domain")
				.create("b")
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

	public boolean checkDomain(String name) {
		Domain domain = new Domain(name);

		System.out.println(String.format(
				"    domain is '%s'",
				domain
		));

		if (null != getDomains()) {
			System.out.println("    checking against domain list...");
			Domain domainsDomain = domain.findParentIn(getDomains().getDomains());

			if (null != domainsDomain) {
				System.out.println(String.format(
						"    ... domain is on domain list (via '%s')",
						domainsDomain
				));

				if (null != getExceptions()) {
					System.out.println("    checking against exception list...");
					Domain exceptionsDomain = domain.findParentIn(getExceptions().getDomains());

					if (null != exceptionsDomain) {
						System.out.println(String.format(
								"    ... domain is on exception list (via '%s')",
								exceptionsDomain
						));

						return false;
					}
					else {
						System.out.println("    ... domain is NOT on exception list");
					}
				}
				else {
					System.out.println("    no exception list loaded");
				}

				return true;
			}
			else {
				System.out.println("    ... domain is NOT on domain list");
			}
		}
		else {
			System.out.println("    no domain list loaded");
		}

		return false;
	}

	@Override
	public void run() {
		if (loadDomains()) {
			System.out.println(String.format(
					">>> %d domain(s) loaded",
					getDomains().size()
			));
			System.out.println();

			if (Main.getCommandLine().hasOption("remove-redundant")) {
				System.out.println("*** remove redundant domain list entries");
				DomainList redundantDomains = new DomainList(getDomains().removeRedundant());

				System.out.println(String.format(
						">>> %d domain(s) (removed %d redundant domain(s))",
						getDomains().size(),
						redundantDomains.size()
				));
				System.out.println();
			}
		}

		if (loadExceptions()) {
			System.out.println(String.format(
					">>> %d exception(s) loaded",
					getExceptions().size()
			));
			System.out.println();

			if (Main.getCommandLine().hasOption("remove-redundant")) {
				System.out.println("*** remove redundant exception list entries");
				DomainList redundantExceptions = new DomainList(getExceptions().removeRedundant());

				System.out.println(String.format(
						">>> %d exception(s) (removed %d redundant exception(s))",
						getExceptions().size(),
						redundantExceptions.size()
				));
				System.out.println();
			}
		}

		if (null != getDomains() && null != getExceptions()) {
			System.out.println("*** analyze domains and exceptions");

			if (Main.getCommandLine().hasOption("remove-obsolete-domains")
					|| Main.getCommandLine().hasOption("remove-unused-exceptions")) {
				int obsoleteDomainsCount = 0;
				int unusedExceptionsCount = 0;

				Iterator<Domain> exceptionsIterator = getExceptions().iterator();
				while (exceptionsIterator.hasNext()) {
					Domain exception = exceptionsIterator.next();

					SortedSet<Domain> obsoleteDomains = getDomains().findChildrenOf(exception);

					if (0 < obsoleteDomains.size()) {
						if (Main.getCommandLine().hasOption("remove-obsolete-domains")) {
							getDomains().remove(obsoleteDomains);
							obsoleteDomainsCount += obsoleteDomains.size();

							if (Main.getCommandLine().hasOption("verbose")) {
								System.out.println(String.format(
										"    ... removing %d obsolete domain(s) (from exception '%s'):",
										obsoleteDomains.size(),
										exception
								));
								System.out.println("            " + DomainList.toString(obsoleteDomains));
							}
						}
					}

					if (0 == obsoleteDomains.size() || Main.getCommandLine().hasOption("remove-obsolete-domains")) {
						Domain parent = exception.findParentIn(getDomains().getDomains());
						if (null == parent) {
							if (Main.getCommandLine().hasOption("remove-unused-exceptions")) {
								exceptionsIterator.remove();
								unusedExceptionsCount++;

								if (Main.getCommandLine().hasOption("verbose")) {
									if (0 < obsoleteDomains.size()) {
										System.out.println(String.format(
												"    ... removing unused exception '%s' (no domain(s) left)",
												exception
										));
									}
									else {
										System.out.println(String.format(
												"    ... removing unused exception '%s'",
												exception
										));
									}
								}
							}
						}
					}
				}

				System.out.println(String.format(
						">>> %d domain(s) (removed %d obsolete domain(s))",
						getDomains().size(),
						obsoleteDomainsCount
				));
				System.out.println(String.format(
						">>> %d exception(s) (removed %d unused exception(s))",
						getExceptions().size(),
						unusedExceptionsCount
				));
			}
			System.out.println();
		}

		if (saveDomains()) {
			System.out.println(String.format(
					">>> %d domain(s) saved",
					getDomains().size()
			));
			System.out.println();
		}

		if (saveExceptions()) {
			System.out.println(String.format(
					">>> %d exception(s) saved",
					getExceptions().size()
			));
			System.out.println();
		}

		if (Main.getCommandLine().hasOption("check-whitelist")) {
			System.out.println("*** checking domain in whitelist mode");
			String checkWhitelist = Domain.parse(Main.getCommandLine().getOptionValue("check-whitelist"));

			if (checkDomain(checkWhitelist)) {
				System.out.println(">>> domain is on the whitelist");

				System.out.println(String.format(
						">>> DELIVERY for '%s' ('%s')",
						Main.getCommandLine().getOptionValue("check-whitelist"),
						checkWhitelist
				));
			}
			else {
				System.out.println(">>> domain is NOT on the whitelist");

				System.out.println(String.format(
						">>> NO DELIVERY for '%s' ('%s')",
						Main.getCommandLine().getOptionValue("check-whitelist"),
						checkWhitelist
				));
			}
			System.out.println();
		}

		if (Main.getCommandLine().hasOption("check-blacklist")) {
			System.out.println("*** checking domain in blacklist mode");
			String checkBlacklist = Domain.parse(Main.getCommandLine().getOptionValue("check-blacklist"));

			if (checkDomain(checkBlacklist)) {
				System.out.println(">>> domain is on the blacklist");

				System.out.println(String.format(
						">>> NO DELIVERY for '%s' ('%s')",
						Main.getCommandLine().getOptionValue("check-blacklist"),
						checkBlacklist
				));
			}
			else {
				System.out.println(">>> domain is NOT on the blacklist");

				System.out.println(String.format(
						">>> DELIVERY for '%s' ('%s')",
						Main.getCommandLine().getOptionValue("check-blacklist"),
						checkBlacklist
				));
			}
			System.out.println();
		}
	}
}
