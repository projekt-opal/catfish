package org.dice_research.opal.catfish.cleaner;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
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
			dateFormatEqualizer.processModel(testCase.getModel(), testCase.getDatasetUri());
		}
	}

	@Test
	public void test() throws Exception {
		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
		Model model = JenaModelUtilities.getModelCopy(testCase.getModel());
		String datasetUri = testCase.getDatasetUri();

		Resource dataset = model.getResource(testCase.getDatasetUri());

		List<Literal> datesOriginal = new LinkedList<>();
		getDates(dataset, datesOriginal);

		dateFormatEqualizer.processModel(model, datasetUri);

		List<Literal> datesProcessed = new LinkedList<>();
		getDates(dataset, datesProcessed);

		for (int i = 0; i < datesOriginal.size(); i++) {
			String original = datesOriginal.get(i).getString();
			String processed = datesProcessed.get(i).getString();
			String processedType = datesProcessed.get(i).getDatatypeURI();

			Assert.assertEquals(10, processed.length());
			Assert.assertTrue(original.startsWith(processed));
			Assert.assertEquals("http://www.w3.org/2001/XMLSchema#date", processedType);
		}
	}

	private void getDates(Resource dataset, List<Literal> dates) {
		addDates(dataset, DCTerms.issued, dates);
		addDates(dataset, DCTerms.modified, dates);

		StmtIterator it = dataset.listProperties(DCAT.distribution);
		while (it.hasNext()) {
			addDates(it.next().getObject().asResource(), DCTerms.issued, dates);
			addDates(it.next().getObject().asResource(), DCTerms.modified, dates);
		}
	}

	private void addDates(Resource resource, Property propery, List<Literal> dates) {
		StmtIterator it = resource.listProperties(propery);
		while (it.hasNext()) {
			dates.add(it.next().getObject().asLiteral());
		}
	}

}