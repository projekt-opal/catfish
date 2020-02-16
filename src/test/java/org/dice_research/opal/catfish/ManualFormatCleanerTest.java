package org.dice_research.opal.catfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Manual test to explore parsed values. Uses {@link FormatCleaner}.
 *
 * @author Adrian Wilke
 */
public class ManualFormatCleanerTest {

	File directory = new File("src/test/resources/org/dice_research/opal/catfish");
	File fileEdpFormats = new File(directory, "edp-formats.txt");
	File fileEdpmediaTypes = new File(directory, "edp-mediaTypes.txt");
	File fileEdpmediaTypesExt = new File(directory, "edp-mediaTypesExt.txt");

	Map<String, Integer> edpFormats;
	Map<String, Integer> edpMediaTypes;
	Map<String, Integer> edpMediaTypesExt;

	@Before
	public void setUp() throws Exception {
		edpFormats = read(fileEdpFormats);
		edpMediaTypes = read(fileEdpmediaTypes);
		edpMediaTypesExt = read(fileEdpmediaTypesExt);
	}

	@Test
	public void testPrint_edpFormats() throws Exception {
		Assume.assumeTrue(true);
		printResults(edpFormats.keySet(), "edpFormats");
	}

	@Test
	public void testPrint_edpMediaTypes() throws Exception {
		Assume.assumeTrue(true);
		printResults(edpMediaTypes.keySet(), "edpMediaTypes");
	}

	@Test
	public void testPrint_edpMediaTypesExt() throws Exception {
		Assume.assumeTrue(true);
		printResults(edpMediaTypesExt.keySet(), "edpMediaTypesExt");
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