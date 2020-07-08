package org.dice_research.opal.catfish.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.dice_research.opal.catfish.service.impl.UriRewriter;
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
	public void test() {
		Model model = JenaModelUtilities.getModelCopy(testCaseEdp.getModel());
		Resource originalDataset = ResourceFactory.createResource(testCaseEdp.getDatasetUri());
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

		UriRewriter uriRewriter = new UriRewriter(Catalogs.ID_EUROPEANDATAPORTAL);
		uriRewriter.clean(model);

		// Original triples with new URIs and Opal.PROP_ORIGINAL_URI triples
		Assert.assertEquals(originalSize + originalDatasets + originalDistributions, model.size());

		int oldDatasetObject = 0;
		stmtIterator = model.listStatements(new SimpleSelector(null, null, originalDataset));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			oldDatasetObject++;
		}

		int oldDatasetSubject = 0;
		stmtIterator = model.listStatements(new SimpleSelector(originalDataset, null, null, ""));
		while (stmtIterator.hasNext()) {
			stmtIterator.next();
			oldDatasetSubject++;
		}

		// Only in Opal.PROP_ORIGINAL_URI triples
		Assert.assertEquals(1, oldDatasetObject);
		Assert.assertEquals(0, oldDatasetSubject);
	}

}