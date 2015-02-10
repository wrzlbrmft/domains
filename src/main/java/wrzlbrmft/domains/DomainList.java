package wrzlbrmft.domains;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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

	public SortedSet<Domain> getDomains() {
		return domains;
	}

	public void setDomains(SortedSet<Domain> domains) {
		this.domains = domains;
	}

	@Override
	public Iterator<Domain> iterator() {
		return getDomains().iterator();
	}

	public boolean load(String fileName) {
		try {
			List<String> lines = FileUtils.readLines(FileUtils.getFile(fileName));
			for (String line : lines) {
				line = line.trim();

				if (StringUtils.isNotBlank(line)) {
					if (!line.contains(" ")) {
						Domain domain = new Domain(Domain.parse(line));

						if (!getDomains().contains(domain)) {
							getDomains().add(domain);
						}
					}
				}
			}
			return true;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}
