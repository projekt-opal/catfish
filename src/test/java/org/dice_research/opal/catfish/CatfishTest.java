package org.dice_research.opal.catfish;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.config.CleaningConfig;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.common.constants.Catalogs;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class CatfishTest {

	@Test
	public void testRemoveNonDeEnTitleDatasets() throws IOException {
		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");

		// Enable additionally
		Model modelCopy = JenaModelUtilities.getModelCopy(testCase.getModel());
		Catfish catfish = new Catfish(new CleaningConfig().setRemoveNonDeEnTitleDatasets(true));
		catfish.processModel(modelCopy, testCase.getDatasetUri());
		Assert.assertEquals(0, modelCopy.size());

		// Enable all
		modelCopy = JenaModelUtilities.getModelCopy(testCase.getModel());
		catfish = new Catfish(new CleaningConfig() //
				.setCatalogIdToReplaceUris(Catalogs.ID_EUROPEANDATAPORTAL) //
				.setCleanEmptyBlankNodes(true) //
				.setCleanFormats(true) //
				.setCleanLiterals(true) //
				.setEqualizeDateFormats(true) //

				.setRemoveNonDeEnTitleDatasets(true));
		catfish.processModel(modelCopy, testCase.getDatasetUri());
		Assert.assertEquals(0, modelCopy.size());
	}

	@Test
	public void testGivenModel_WhenCleanWithAllCleanables_ThenReturnCleanedModel() throws IOException {
		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
		Model copyModel = JenaModelUtilities.getModelCopy(testCase.getModel());
		new Catfish(null).processModel(testCase.getModel(), testCase.getDatasetUri());
		Assert.assertTrue("graph size must be less than the original graph",
				testCase.getModel().size() < copyModel.size());
	}

	@Test
	public void testGivenModel_WhenContainAllCases_ThenReturnCleanedModel() {
		Model model = ModelFactory.createDefaultModel();
		model.read("org/dice_research/opal/catfish/dataSetWithLanguageAndDataTypeAndEmptyLiteral.ttl");
		Model copyModel = JenaModelUtilities.getModelCopy(model);
		new Catfish(null).processModel(model, null);
		Assert.assertTrue("graph size must be less than the original graph", model.size() < copyModel.size());
	}

	@Test
	public void testDataFormatEqualizer() throws Exception {

		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
		Resource dataset = ResourceFactory.createResource(testCase.getDatasetUri());
		Model model = JenaModelUtilities.getModelCopy(testCase.getModel());

		// Check if original model contains date
		boolean hasDate = false;
		boolean hasDateTime = false;
		NodeIterator nodeIterator = model.listObjectsOfProperty(dataset, DCTerms.issued);
		while (nodeIterator.hasNext()) {
			RDFNode rdfNode = nodeIterator.next();
			if (rdfNode.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#date")) {
				hasDate = true;
			}
			if (rdfNode.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
				hasDateTime = true;
			}
		}
		Assert.assertFalse(hasDate);
		Assert.assertTrue(hasDateTime);

		// Check if processed model does NOT contain date
		Model copyModel = JenaModelUtilities.getModelCopy(testCase.getModel());
		new Catfish(new CleaningConfig().setEqualizeDateFormats(true)).processModel(copyModel, null);
		hasDate = false;
		hasDateTime = false;
		nodeIterator = copyModel.listObjectsOfProperty(dataset, DCTerms.issued);
		while (nodeIterator.hasNext()) {
			RDFNode rdfNode = nodeIterator.next();
			if (rdfNode.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#date")) {
				hasDate = true;
			}
			if (rdfNode.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
				hasDateTime = true;
			}
		}
		Assert.assertTrue(hasDate);
		Assert.assertFalse(hasDateTime);
	}

	@Test
	public void testUriRewriting() throws IOException {
		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "orenhofen");
		Model model = JenaModelUtilities.getModelCopy(testCase.getModel());

		long oldSize = model.size();

		ResIterator datasets = model.listSubjectsWithProperty(RDF.type, DCAT.Dataset);
		while (datasets.hasNext()) {
			Assert.assertTrue(datasets.next().getURI().equals(testCase.getDatasetUri()));
		}

		Catfish catfish = new Catfish(new CleaningConfig()

				.setCatalogIdToReplaceUris(Catalogs.ID_EUROPEANDATAPORTAL)

				.setCleanEmptyBlankNodes(false).setCleanFormats(false).setCleanLiterals(false)
				.setEqualizeDateFormats(false)

		);
		catfish.processModel(model, testCase.getDatasetUri());

		Assert.assertTrue(oldSize < model.size());

		datasets = model.listSubjectsWithProperty(RDF.type, DCAT.Dataset);
		while (datasets.hasNext()) {
			Assert.assertFalse(datasets.next().getURI().equals(testCase.getDatasetUri()));
		}

		Assert.assertTrue(catfish.getNewDatasetUri() != null);
		Assert.assertTrue(model.containsResource(ResourceFactory.createResource(catfish.getNewDatasetUri())));
	}

}