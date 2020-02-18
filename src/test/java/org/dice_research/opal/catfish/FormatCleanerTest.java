package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link FormatCleaner}.
 * 
 * @author Adrian Wilke
 */
public class FormatCleanerTest {

	/**
	 * Contains 6 distributions with:
	 * 
	 * 3x dct:format "PDF" AND dcat:mediaType "application/pdf"
	 * 
	 * 3x dct:format "HTML" AND dcat:mediaType "text/html"
	 */
	TestCase testCaseA;

	@Before
	public void setUp() throws Exception {
		testCaseA = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
	}

	Model getModelCopy(TestCase testCase) {
		Model model = ModelFactory.createDefaultModel();
		model.add(testCase.getModel());
		return model;
	}

	@Test
	public void test() throws Exception {
		Model testModel;
		String testDatasetUri;
		Resource testDataset;

		// Direct FormatCleaner test

		testModel = getModelCopy(testCaseA);
		testDatasetUri = testCaseA.getDatasetUri();
		testDataset = testModel.getResource(testDatasetUri);

		new FormatCleaner().clean(testModel, testDataset);

		// 6 triples for formats of 6 distributions (3xhtml, 3xpdf)
		// 2 triples for types of formats (html, pdf)
		Assert.assertEquals("8 additional format triples", testCaseA.getModel().size() + 6 + 2, testModel.size());

		// Catfish test

		testModel = getModelCopy(testCaseA);
		testDatasetUri = testCaseA.getDatasetUri();
		testDataset = testModel.getResource(testDatasetUri);

		new Catfish().cleanFormats(true).removeEmptyBlankNodes(false).removeEmptyLiterals(false).processModel(testModel,
				testDatasetUri);
		Assert.assertEquals("8 additional format triples", testCaseA.getModel().size() + 6 + 2, testModel.size());

		// Manual checks
		if (Boolean.FALSE) {
			testModel.write(System.out, "TURTLE");
		}
	}

	@Test
	public void testDownloadUrl() throws Exception {

		Model model = ModelFactory.createDefaultModel();
		Resource dataset = ResourceFactory.createResource("http://example.com/dataset");
		Resource distribution = ResourceFactory.createResource("http://example.com/distribution");

		model.add(dataset, RDF.type, DCAT.Dataset);

		model.add(distribution, RDF.type, DCAT.Distribution);
		model.add(dataset, DCAT.distribution, distribution);

		model.add(distribution, DCAT.downloadURL,
				ResourceFactory.createPlainLiteral("http://example.com/download.php"));
		model.add(distribution, DCAT.downloadURL,
				ResourceFactory.createPlainLiteral("http://example.com/download.get?something=true"));
		model.add(distribution, DCAT.downloadURL,
				ResourceFactory.createPlainLiteral("http://example.com/download.php/dataset.xml"));

		long originalSize = model.size();
		new FormatCleaner().clean(model, dataset);

		// XML: downloadURL and type
		Assert.assertEquals("XML format", originalSize + 2, model.size());

		// Manual checks
		if (Boolean.FALSE) {
			model.write(System.out, "TURTLE");
		}
	}

}