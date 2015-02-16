# domains

*domains* is a command-line tool written in Java for analysis and optimization
of domain lists, e.g. to be used as whitelists or blacklists.

## Features

* Auto-correct malformed domain names
* Sorting
* Remove duplicate list entries
* Remove redundant list entries
* Check against an exclusion list and
  * Remove obsolete domain list entries
  * Remove unused exception list entries
* Save optimized lists as new files

## Download

A pretty up-to-date version of *domains* can be downloaded
[here](http://wrzlbrmft.de/github/wrzlbrmft/domains/domains.jar).

To build the latest version by yourself, see below for *Build Instructions*.

## Usage

Having installed the
[Java Runtime Environment](http://java.com/de/download/manual.jsp) 7+, you can
run *domains* at the command-line or in Terminal with:

```
java -jar domains.jar
```

Append `-h` or `--help` to get a list of all available options:

```
java -jar domains.jar -h
```

All available options are:

```
 -d,--domains <file>             load domain list from text file
 -e,--exceptions <file>          load exception list from text file
 -h,--help                       print this help message and exit
 -o,--remove-obsolete-domains    remove obsolete domain list entries (e.g.
                                 if ".com" is on the exception list, the
                                 domain list entry ".foo.com" is obsolete
                                 and removed)
 -r,--remove-redundant           remove redundant list entries (e.g.
                                 ".com" includes ".foo.com", so ".foo.com"
                                 is redundant and removed)
 -s,--save-domains <file>        save optimized domain list as new text
                                 file
 -u,--remove-unused-exceptions   remove unused exception list entries
                                 (e.g. if ".com" is NOT on the domain
                                 list, the exception list entry ".foo.com"
                                 is unused and removed)
 -v,--verbose                    be more verbose
    --version                    print version info and exit
 -x,--save-exceptions <file>     save optimized exception list as new text
                                 file
```

### Quick Start

Load the domain list from `domains.txt`, auto-correct and sort it, then remove
both duplicate and redundant entries. Finally save the optimized domain list as
`domains-optimized.txt`:

```
java -jar domains.jar -d domains.txt -r -s domains-optimized.txt
```

Read further for more available optimizations.

## Domain Lists

A domain list is a simple text file, containing one domain name per line:

**Example**

```
.foo.com
.bar.com
.www.xyz.net
.org
```

Each domain name usually starts with a dot (`.`) and includes all of its
sub-domains. E.g. `.foo.com` includes `.www.foo.com`, `.bar.foo.com` etc.

### Exception Lists

*domains* also supports exception lists to express rules like *"'.com' except
'.youtube.com'"*. An exception list is a second file loaded in conjunction with
a domain list; also a simple text file, containing one (exception) domain name
per line.

## Optimizations

### Domain and Exception Lists

The following optimizations can be applied to both domain and exception lists.

#### Auto-Correct Malformed Domain Names

*(Auto-correction is always applied to any list loaded.)*

Malformed domain names are auto-corrected with a set of rules applied in the
following order:

1. remove the last `://` and everything before it
2. remove the first `:` and everything after it
3. remove the first `/` and everything after it
4. ensure that the domain name starts with a dot (`.`)
5. change to lower-case letters

**NOTE:** Because of rule #4, `[.]foo.com` does not include `[.]barfoo.com` even
if you put `foo.com` on a list without the leading dot.

**Example**

All of the following entries are auto-corrected to `.www.foo.com`:

```
http://www.foo.com/
WWW.FOO.COM/bar
.www.foo.com:8080
https://www.foo.com/bar/index.html
```

#### Sorting

*(Sorting is always applied to any list loaded.)*

Domain names are sorted as reverse-strings (`.foo.com` as `moc.oof.`) to keep
different sub-domains next to each other.

**Example**

```
.www.foo.com
.bar.net
.ftp.foo.com
.www.bar.net
.foo.com
```

becomes

```
(tba)
```

#### Remove Duplicate List Entries

*(De-duplication is always applied to any list loaded.)*

Each domain name is only allowed to appear once on a list.

**NOTE:** Due to auto-correction, list entries can also result in the same
domain name, then being de-duplicated.

#### Remove Redundant List Entries

Domain names always include all their sub-domains. Therefore, in the following
list, all entries except `.com` are redundant:

```
.foo.com
.com
.bar.com
```

Use the `-r` or `--remove-redundant` command-line options to remove the
redundant list entries.

### Exception Lists

The following optimizations can only be applied when loading a domain list *and*
an exception list.

Use the `-e` or `--exceptions` command-line option to load an exception list.

**Example**

Load the domain list from `domains.txt` and the exception list from
`exceptions.txt`:

```
java -jar domains.jar -d domains.txt -e exceptions.txt
```

#### Remove Obsolete Domain List Entries

Any domain list entry included in an exception list entry is obsolete.

**Example**

Domain list:
```
.www.foo.com
```

Exception list:
```
.foo.com
```

Since the exception `.foo.com` includes `.www.foo.com` on the domain list,
`.www.foo.com` can be removed from the domain list.

Use the `-o` or `--remove-obsolete-domains` command-line option to remove the
obsolete domain list entries.

#### Remove Unused Exception List Entries

Any exception not being a sub-domain of a domain list entry is unsed.

**Example**

Domain list:
```
.foo.com
```

Exception list:
```
.bar.com
```

Since `.bar.com` is not a sub-domain of any domain list entry, it can be removed
from the exception list.

Use the `-u` or `--remove-unused-exceptions` command-line option to remove the
unused exception list entries.

### Save Optimized Lists

All optimizations are applied to copies of the domain and/or exception list
files loaded into memory. The original files are never changed but you can save
the optimized lists from memory as new files.

Use the `-s` or `--save-domains` command-line option to save the optimized
domain list, and the `-x` or `--save-exceptions` command-line option to save the
optimized exception list as a new file.

**Example**

Load the domain list from `domains.txt` and save the optimized domain list as
`domains-optimized.txt`:

```
java -jar domains.jar -d domains.txt -s domains-optimized.txt
```

**NOTE:** Any existing new file will be overwritten.

## Build Instructions

A pretty up-to-date version of *domains* can be downloaded
[here](http://wrzlbrmft.de/github/wrzlbrmft/domains/domains.jar).

Or you can easily build the latest version by yourself.

**Requirements**

* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
7+
* [Maven](http://maven.apache.org/)
* maybe [Git](http://git-scm.com/)

Download the latest source code as a [ZIP
file](https://github.com/wrzlbrmft/domains/archive/master.zip) or use Git:

```
git clone https://github.com/wrzlbrmft/domains.git
```

Change into the unzipped or the checkout directory and run Maven:

```
mvn package
```

The uber-jar containing both the compiled source code and all of its
dependencies is saved in the `target/` directory.

Simply run it:

```
java -jar target/domains.jar
```
