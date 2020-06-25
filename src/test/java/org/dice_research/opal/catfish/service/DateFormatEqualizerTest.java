package org.dice_research.opal.catfish.service;

import java.util.List;

import org.dice_research.opal.catfish.service.impl.DateFormatEqualizer;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DateFormatEqualizer}.
 * 
 * @author Adrian Wilke
 */
public class DateFormatEqualizerTest {

	DateFormatEqualizer dateFormatEqualizer;

	@Before
	public void setUp() throws Exception {
		dateFormatEqualizer = new DateFormatEqualizer();
	}

	/**
	 * Tests if an exception is thrown on the default test cases.
	 */
	@Test
	public void testDefaultTestCases() throws Exception {
		List<TestCase> testCases = OpalTestCases.getAllTestCases();
		for (TestCase testCase : testCases) {
			dateFormatEqualizer.clean(testCase.getModel());
		}
	}

}
