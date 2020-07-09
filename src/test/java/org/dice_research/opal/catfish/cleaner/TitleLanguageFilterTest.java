package org.dice_research.opal.catfish.cleaner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link TitleLanguageFilter}.
 *
 * @author Adrian Wilke
 */
public class TitleLanguageFilterTest {

	TestCase testCaseEdpA;
	TestCase testCaseEdpB;

	@Before
	public void setUp() throws Exception {
		testCaseEdpA = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
		testCaseEdpB = OpalTestCases.getTestCase("edp-2019-12-17", "mittenwalde");
	}

	/**
	 * Has de title and empty en title.
	 */
	@Test
	public void testA() throws Exception {
		Model model = JenaModelUtilities.getModelCopy(testCaseEdpA.getModel());
		String datasetUri = testCaseEdpA.getDatasetUri();
		new TitleLanguageFilter().processModel(model, datasetUri);
		Assert.assertEquals(0, model.size());
	}

	/**
	 * Has de title.
	 */
	@Test
	public void testB() throws Exception {
		Model model = JenaModelUtilities.getModelCopy(testCaseEdpB.getModel());
		String datasetUri = testCaseEdpB.getDatasetUri();
		new TitleLanguageFilter().processModel(model, datasetUri);
		Assert.assertEquals(0, model.size());
	}

	@Test
	public void testDeAndEnAndAnotherTitle() throws Exception {
		// Has de title
		Model model = JenaModelUtilities.getModelCopy(testCaseEdpB.getModel());
		String datasetUri = testCaseEdpB.getDatasetUri();
		Resource dataset = model.getResource(datasetUri);
		long originalSize = model.size();

		// Add en title
		dataset.addLiteral(DCTerms.title, ResourceFactory.createLangLiteral("my title", "en"));

		// Add another title to be removed
		dataset.addLiteral(DCTerms.title, ResourceFactory.createLangLiteral("x", "languageToRemove"));
		new TitleLanguageFilter().processModel(model, datasetUri);

		Assert.assertEquals(originalSize + 1, model.size());
	}

}
