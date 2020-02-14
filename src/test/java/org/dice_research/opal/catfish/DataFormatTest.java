package org.dice_research.opal.catfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class DataFormatTest {

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

	/**
	 * TODO
	 * 
	 * check https://www.iana.org/assignments/media-types/media-types.xhtml
	 * 
	 * check prefix http://publications.europa.eu/resource/authority/file-type/
	 * 
	 * check ASCII chars and numbers
	 * 
	 * check slashes, if not http prefix
	 * 
	 * create OPAL Class for cleaned types. Save cleaned types as dct:format, as
	 * mediaType is restricted and can not include all types.
	 * 
	 * Remove prefix dots
	 * 
	 * Create unknown type as default
	 */
	@Test
	public void testEmptyLiterals() throws Exception {
		if (Boolean.FALSE)
			for (Entry<String, Integer> entry : edpMediaTypes.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}

		Set<String> formats = new HashSet<>();
		Set<String> formats2 = new HashSet<>();

		for (String format : edpFormats.keySet()) {
			if (format.trim().length() > 0 && format.length() <= 4) {
				formats.add(format.toLowerCase());
			} else {
				formats2.add(format.toLowerCase());
			}
		}
		for (String format : edpMediaTypes.keySet()) {
			if (format.trim().length() > 0 && format.length() <= 4) {
				formats.add(format.toLowerCase());
			} else {
				formats2.add(format.toLowerCase());
			}
		}
		for (String format : edpMediaTypesExt.keySet()) {
			if (format.trim().length() > 0 && format.length() <= 4) {
				formats.add(format.toLowerCase());
			} else {
				formats2.add(format.toLowerCase());
			}
		}

		System.out.println(formats);
		System.out.println(formats2);
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
}