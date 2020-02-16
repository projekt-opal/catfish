package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link StructuralCleaner}.
 * 
 * @author Adrian Wilke
 */
public class StructuralCleanerTest {

	/**
	 * Test model contains empty literals (with language tags).
	 */
	TestCase testCaseA;

	/**
	 * Two distributions have property DCTerms.rights with empty blank nodes as
	 * objects.
	 */
	TestCase testCaseB;

	@Before
	public void setUp() throws Exception {
		testCaseA = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
		testCaseB = OpalTestCases.getTestCase("edp-2019-12-17", "mittenwalde");
	}

	Model getModelCopy(TestCase testCase) {
		Model model = ModelFactory.createDefaultModel();
		model.add(testCase.getModel());
		return model;
	}

	/**
	 * Tests, if empty literals are removed.
	 */
	@Test
	public void testEmptyLiterals() throws Exception {
		Resource dataset = testCaseA.getModel().getResource(testCaseA.getDatasetUri());
		Model testModel;

		testModel = getModelCopy(testCaseA);
		new Catfish().removeEmptyLiterals(true).processModel(testModel, testCaseA.getDatasetUri());
		Assert.assertTrue("Catfish removes empty literal", testModel.size() < testCaseA.getModel().size());

		testModel = getModelCopy(testCaseA);
		new StructuralCleaner(new Catfish()).clean(testModel);
		Assert.assertTrue("StructuralCleaner removes empty literal", testModel.size() < testCaseA.getModel().size());

		Literal emptyEnLiteral = ResourceFactory.createLangLiteral("", "en");
		Assert.assertTrue("Empty title in source",
				testCaseA.getModel().contains(dataset, DCTerms.title, emptyEnLiteral));
		Assert.assertFalse("No empty title", testModel.contains(dataset, DCTerms.title, emptyEnLiteral));

		// Manual checks
		if (Boolean.FALSE) {
			System.out.println("Lit: Source " + testCaseA.getModel().size() + ", cleaned " + testModel.size());
		}
		if (Boolean.FALSE) {
			testCaseA.getModel().write(System.out, "TURTLE");
			System.out.println("---");
			testModel.write(System.out, "TURTLE");
		}
	}

	/**
	 * Tests, if empty blank nodes are removed.
	 */
	@Test
	public void testEmptyBlankNodes() throws Exception {
		String distWithEbnUri = "https://europeandataportal.eu/set/distribution/1007b442-bf33-407f-ab74-534ca67d79e3";
		Model testModel;

		testModel = getModelCopy(testCaseB);
		new Catfish().processModel(testModel, testCaseB.getDatasetUri());
		Assert.assertTrue("Catfish removes empty blank node", testModel.size() < testCaseB.getModel().size());

		testModel = getModelCopy(testCaseB);
		new StructuralCleaner(new Catfish()).clean(testModel);
		Assert.assertTrue("StructuralCleaner removes empty blank node", testModel.size() < testCaseB.getModel().size());

		StmtIterator stmtIterator = testCaseB.getModel().getResource(distWithEbnUri).listProperties(DCTerms.rights);
		Assert.assertTrue("Empty blank node in source", stmtIterator.hasNext());

		stmtIterator = testModel.getResource(distWithEbnUri).listProperties(DCTerms.rights);
		Assert.assertFalse("No empty blank node", stmtIterator.hasNext());

		// Manual checks
		if (Boolean.FALSE) {
			System.out.println("EBN: Source " + testCaseB.getModel().size() + ", cleaned " + testModel.size());
		}
		if (Boolean.FALSE) {
			testCaseB.getModel().write(System.out, "TURTLE");
			System.out.println("---");
			testModel.write(System.out, "TURTLE");
		}
	}

}