package org.dice_research.opal.catfish.manual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.dice_research.opal.catfish.cleaner.FormatCleaner;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Manual test to explore parsed values. Uses {@link FormatCleaner}.
 *
 * @author Adrian Wilke
 */
public class ManualFormatCleanerTest {

	public final static List<String> EXTENSIONS_BLACKLIST;

	static {
		String[] extensions = { "asp", "aspx", "cgi", "dll", "htm", "html", "jsp", "jspx", "php", "php3", "php4",
				"phtml", "pl", "py", "rb", "rhtml", "shtml", "xhtml", "xhtml", "download", "dl", "query", "get", "post",
				"form", "api", "diverse", "document", "htlm", "html5", "multi", "multiformat", "sparql", "www", "ashx",
				"search" };
		EXTENSIONS_BLACKLIST = Arrays.asList(extensions);
	}

	File directory = new File("src/test/resources/org/dice_research/opal/catfish");
	File fileEdpFormats = new File(directory, "edp-formats.txt");
	File fileEdpmediaTypes = new File(directory, "edp-mediaTypes.txt");
	File fileEdpmediaTypesExt = new File(directory, "edp-mediaTypesExt.txt");
	File fileDownloasUrls = new File(directory, "edp-downloasUrls.txt");

	Map<String, Integer> edpFormats;
	Map<String, Integer> edpMediaTypes;
	Map<String, Integer> edpMediaTypesExt;
	List<String> edpDownloadUrls;

	@Before
	public void setUp() throws Exception {
		edpFormats = read(fileEdpFormats);
		edpMediaTypes = read(fileEdpmediaTypes);
		edpMediaTypesExt = read(fileEdpmediaTypesExt);
		edpDownloadUrls = readUrls(fileDownloasUrls);
	}

	@Test
	public void testPrint_edpFormats() throws Exception {
		Assume.assumeTrue(false);
		printResults(edpFormats.keySet(), "edpFormats");
	}

	@Test
	public void testPrint_edpMediaTypes() throws Exception {
		Assume.assumeTrue(false);
		printResults(edpMediaTypes.keySet(), "edpMediaTypes");
	}

	@Test
	public void testPrint_edpMediaTypesExt() throws Exception {
		Assume.assumeTrue(false);
		printResults(edpMediaTypesExt.keySet(), "edpMediaTypesExt");
	}

	@Test
	public void testPrintExtensionWhitelist() throws Exception {
		Assume.assumeTrue(false);
		Set<String> whitelist = new TreeSet<>();
		for (String string : edpFormats.keySet()) {
			whitelist.addAll(new FormatCleaner().cleanInput(string));
		}
		for (String string : edpMediaTypes.keySet()) {
			whitelist.addAll(new FormatCleaner().cleanInput(string));
		}
		for (String string : edpMediaTypesExt.keySet()) {
			whitelist.addAll(new FormatCleaner().cleanInput(string));
		}
		whitelist.removeAll(EXTENSIONS_BLACKLIST);
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : whitelist) {
			stringBuilder.append(string);
			stringBuilder.append(System.lineSeparator());
		}
		System.out.println(stringBuilder);
	}

	@Test
	public void testPrint_edpDownloadUrls() throws Exception {
		Assume.assumeTrue(false);
		Set<String> extensions = new TreeSet<>();
		Set<String> in = new TreeSet<>();
		Set<String> ex = new TreeSet<>();
		for (String url : edpDownloadUrls) {
			String cleaned = new FormatCleaner().cleanDownloadUrl(url);
			if (cleaned == null) {
				ex.add(url);
			} else {
				in.add(url);
				extensions.add(cleaned);
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (String string : extensions) {
			stringBuilder.append(string);
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append(System.lineSeparator());
		for (String string : in) {
			stringBuilder.append(string);
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append(System.lineSeparator());
		for (String string : ex) {
			stringBuilder.append(string);
			stringBuilder.append(System.lineSeparator());
		}
		System.out.println(stringBuilder);
	}

	public void printResults(Collection<String> values, String title) throws Exception {
		ValueFilter valueFilter = new ValueFilter().setTitle(title);
		for (String value : values) {
			Set<String> formats = new FormatCleaner().cleanInput(value);
			valueFilter.filter(formats, value.toLowerCase().trim());
		}

		valueFilter.printChanged();
		valueFilter.printSame();
		valueFilter.printIgnored();
	}

	protected Map<String, Integer> read(File file) throws IOException {
		Map<String, Integer> map = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line = bufferedReader.readLine();
			boolean newEntry = true;
			String key = null;
			while (line != null) {
				if (line.startsWith("#")) {
					// Comment: Just read next line below
				} else if (newEntry) {
					// Remove type information from key
					key = toLiteral(line);
					newEntry = false;
				} else {
					// Insert or update entry
					if (map.containsKey(key)) {
						map.put(key, Integer.parseInt(line) + map.get(key));
					}
					map.put(key, Integer.parseInt(line));
					newEntry = true;
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
		return sortByValue(map);
	}

	protected List<String> readUrls(File file) throws IOException {
		List<String> list = new LinkedList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				if (line.startsWith("#")) {
					// Comment: Just read next line below
				} else {
					list.add(line);
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
		return list;
	}

	protected String toLiteral(String string) {
		int begin = string.indexOf('"');
		int end = string.lastIndexOf('"');
		int type = string.indexOf("\"^^");

		if (begin == 0 && end == string.length() - 1) {
			return string.substring(1, string.length() - 1);
		} else if (type != -1) {
			return string.substring(1, type);
		}
		return string;
	}

	protected static Map<String, Integer> sortByValue(final Map<String, Integer> wordCounts) {
		return wordCounts.entrySet().stream().sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	protected static Map<String, String> sortByStringValue(final Map<String, String> wordCounts) {
		return wordCounts.entrySet().stream().sorted((Map.Entry.<String, String>comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	/**
	 * Filters changed values.
	 */
	public class ValueFilter {
		public String title;
		public Map<String, Set<String>> changed = new HashMap<>();
		public Set<String> same = new TreeSet<>();
		public Set<String> ignored = new TreeSet<>();

		public ValueFilter setTitle(String title) {
			this.title = title;
			return this;
		}

		public void filter(Set<String> processedFormats, String input) {
			if (input == null) {
				// Ignore
			} else if (processedFormats.isEmpty()) {
				ignored.add(input);
			} else if (processedFormats.size() == 1 && processedFormats.iterator().next().equals(input)) {
				same.add(processedFormats.iterator().next());
			} else {
				if (!changed.containsKey(input)) {
					changed.put(input, new HashSet<>());
				}
				changed.get(input).addAll(processedFormats);
			}
		}

		public void printChanged() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(System.lineSeparator());
			if (title != null) {
				stringBuilder.append(title);
				stringBuilder.append(" ");
			}
			stringBuilder.append("CHANGED");
			stringBuilder.append(System.lineSeparator());
			for (Entry<String, Set<String>> entry : changed.entrySet()) {
				for (String string : entry.getValue()) {
					stringBuilder.append(string);
					separate(stringBuilder, FormatCleaner.EXT_MAX_LENGTH, string.length());
					stringBuilder.append(entry.getKey());
					stringBuilder.append(System.lineSeparator());
				}
			}
			System.out.println(stringBuilder);
		}

		public void printSame() {
			printSet(same, title, "SAME");
		}

		public void printIgnored() {
			printSet(ignored, title, "IGNORED");
		}

		public void printSet(Set<String> set, String title, String type) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(System.lineSeparator());
			if (title != null) {
				stringBuilder.append(title);
				stringBuilder.append(" ");
			}
			stringBuilder.append(type);
			stringBuilder.append(System.lineSeparator());
			for (String string : set) {
				stringBuilder.append(string);
				stringBuilder.append(System.lineSeparator());
			}
			System.out.println(stringBuilder);
		}

		public void separate(StringBuilder stringBuilder, int maxLength, int exclude) {
			for (int i = 0; i < maxLength - exclude; i++) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(" ");
		}
	}
}