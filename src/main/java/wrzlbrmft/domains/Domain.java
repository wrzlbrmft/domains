package wrzlbrmft.domains;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedSet;

public class Domain implements Comparable<Domain> {
	protected String name;

	public Domain(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Domain o) {
		return StringUtils.reverse(getName()).compareTo(StringUtils.reverse(o.getName()));
	}

	public static String parse(String str) {
		String name = str;

		if (name.contains("://")) {
			name = StringUtils.substringAfter(name, "://");
		}

		if (name.contains(":")) {
			name = StringUtils.substringBefore(name, ":");
		}

		if (name.contains("/")) {
			name = StringUtils.substringBefore(name, "/");
		}

		if (!name.startsWith(".")) {
			name = "." + name;
		}

		return name.toLowerCase();
	}

	public Domain findParentIn(SortedSet<Domain> domains) {
		for (Domain domain : domains) {
			if (getName().endsWith(domain.getName())) {
				return domain;
			}
		}
		return null;
	}
}
