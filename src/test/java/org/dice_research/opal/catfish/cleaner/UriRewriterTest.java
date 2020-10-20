package org.dice_research.opal.catfish.cleaner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.DCAT;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.common.constants.Catalogs;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link UriRewriter}.
 * 
 * @author Adrian Wilke
 */
public class UriRewriterTest {

	TestCase testCaseEdp;

	@Before
	public void setUp() throws Exception {
		testCaseEdp = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");
	}

	@Test
	public void test() throws Exception {
		Model model = JenaModelUtilities.getModelCopy(testCaseEdp.getModel());
		String datasetUri = testCaseEdp.getDatasetUri();
		Resource originalDataset = ResourceFactory.createResource(datasetUri);
		long originalSize = model.size();

		int originalDatasets = 0;
		StmtIterator stmtIterator = model.listStatements(new SimpleSelector(null, null, DCAT.Dataset));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			originalDatasets++;
		}

		int originalDistributions = 0;
		stmtIterator = model.listStatements(new SimpleSelector(null, null, DCAT.Distribution));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			originalDistributions++;
		}

		// Process
		UriRewriter uriRewriter = new UriRewriter(Catalogs.ID_EUROPEANDATAPORTAL);
		uriRewriter.processModel(model, datasetUri);

		// Additonal triple pointing to source catalog
		int catalogTriple = 1;

		// Original triples with new URIs and Opal.PROP_ORIGINAL_URI triples
		Assert.assertEquals(originalSize + originalDatasets + originalDistributions + catalogTriple, model.size());

		// Uri rewritten
		Assert.assertTrue(uriRewriter.getNewDatasetUri() != null);
		Assert.assertTrue(model.containsResource(ResourceFactory.createResource(uriRewriter.getNewDatasetUri())));

		int replacedDatasetAsObject = 0;
		stmtIterator = model.listStatements(new SimpleSelector(null, null, originalDataset));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			replacedDatasetAsObject++;
		}

		int replacedDatasetAsSubject = 0;
		stmtIterator = model.listStatements(new SimpleSelector(originalDataset, null, null, ""));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			replacedDatasetAsSubject++;
		}

		// The old dataset URI should only be referenced in one triple
		// (Opal.PROP_ORIGINAL_URI)
		Assert.assertEquals(1, replacedDatasetAsObject);
		Assert.assertEquals(0, replacedDatasetAsSubject);

		// Dev
		if (Boolean.FALSE) {
			RDFDataMgr.write(System.out, model, Lang.TURTLE);
		}
	}

}