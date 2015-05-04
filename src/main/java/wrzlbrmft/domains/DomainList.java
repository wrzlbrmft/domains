package wrzlbrmft.domains;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class DomainList implements Iterable<Domain> {
	protected SortedSet<Domain> domains = new TreeSet<>();

	public DomainList() {}

	public DomainList(String fileName) {
		this();

		load(fileName);
	}

	public DomainList(SortedSet<Domain> domains) {
		this();

		setDomains(domains);
	}

	public SortedSet<Domain> getDomains() {
		return domains;
	}

	public void setDomains(SortedSet<Domain> domains) {
		this.domains = domains;
	}

	public static String toString(SortedSet<Domain> domains) {
		return StringUtils.join(domains, ", ");
	}

	@Override
	public String toString() {
		return toString(getDomains());
	}

	@Override
	public Iterator<Domain> iterator() {
		return getDomains().iterator();
	}

	public List<String> getDomainNames() {
		List<String> domainNames = new ArrayList<>();
		for (Domain domain : getDomains()) {
			domainNames.add(domain.getName());
		}
		return domainNames;
	}

	public boolean load(String fileName) {
		System.out.println(String.format(
				"    reading from file '%s'...",
				fileName
		));

		try {
			List<String> lines = FileUtils.readLines(FileUtils.getFile(fileName));
			for (String line : lines) {
				line = line.trim();

				if (StringUtils.isNotBlank(line)) {
					if (!line.contains(" ")) {
						String parsedDomain = Domain.parse(line);
						if (StringUtils.isNotBlank(parsedDomain)) {
							Domain domain = new Domain(parsedDomain);

							if (!getDomains().contains(domain)) {
								getDomains().add(domain);
							}
							else {
								if (Main.getCommandLine().hasOption("verbose")) {
									System.out.println(String.format(
											"    ... removed duplicate '%s'",
											domain
									));
								}
							}
						}
						else {
							System.out.println(String.format(
									"    ... ignoring auto-correct to NULL '%s'",
									line
							));
						}
					}
				}
			}
			System.out.println("    OK");
			return true;
		}
		catch (IOException e) {
			System.out.println("    ERROR: " + e.getMessage());
		}
		return false;
	}

	public boolean save(String fileName) {
		System.out.println(String.format(
				"    writing to file '%s'...",
				fileName
		));

		try {
			FileUtils.writeLines(FileUtils.getFile(fileName), getDomainNames(), System.lineSeparator());
			System.out.println("    OK");
			return true;
		}
		catch (IOException e) {
			System.out.println("    ERROR: " + e.getMessage());
		}
		return false;
	}

	public SortedSet<Domain> removeRedundant() {
		SortedSet<Domain> uniqueDomains = new TreeSet<>();
		SortedSet<Domain> redundantDomains = new TreeSet<>();

		for (Domain domain : getDomains()) {
			Domain parent = domain.findParentIn(uniqueDomains);
			if (null == parent) {
				uniqueDomains.add(domain);
			}
			else {
				redundantDomains.add(domain);

				if (Main.getCommandLine().hasOption("verbose")) {
					System.out.println(String.format(
							"    ... removed redundant '%s' (from '%s')",
							domain,
							parent
					));
				}
			}
		}
		setDomains(uniqueDomains);

		return redundantDomains;
	}

	public SortedSet<Domain> findChildrenOf(Domain parent) {
		SortedSet<Domain> children = new TreeSet<>();
		for (Domain domain : getDomains()) {
			if (("." + domain.getName()).endsWith("." + parent.getName())) {
				children.add(domain);
			}
		}
		return children;
	}

	public void remove(SortedSet<Domain> domains) {
		for (Domain domain : domains) {
			getDomains().remove(domain);
		}
	}

	public int size() {
		return getDomains().size();
	}
}
