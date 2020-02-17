package org.dice_research.opal.catfish.example;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.dice_research.opal.common.utilities.FileHandler;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Test;

/**
 * Minimal working example.
 *
 * @author Adrian Wilke
 */
public class ExampleTest {

	@Test
	public void test() throws Exception {

		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");

		File turtleInputFile = File.createTempFile(ExampleTest.class.getName(), ".in.txt");
		FileHandler.export(turtleInputFile, testCase.getModel());
//		turtleInputFile.deleteOnExit();

		File turtleOutputFile = File.createTempFile(ExampleTest.class.getName(), "");
//		turtleOutputFile.deleteOnExit();

		Example example = new Example();
		example.cleanMetadata(turtleInputFile, turtleOutputFile, testCase.getDatasetUri());

		assertTrue(turtleOutputFile.exists());
		assertNotEquals(turtleInputFile.length(), turtleOutputFile.length());
	}

}