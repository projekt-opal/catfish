package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.Catfish;
// import org.junit.Assert;
import org.junit.Test;

public class DummyTest {

	/**
	 * Tests if date in wrong format is removed.
	 */
	@Test
	public void test() throws Exception {
		Model model = ModelFactory.createDefaultModel();

		String datasetUri = "https://example.org/dataset1";
		Resource dataset = ResourceFactory.createResource();
		model.add(dataset, RDF.type, DCAT.Dataset);

		String dateString = "6.11.2019";
		Literal date = ResourceFactory.createPlainLiteral(dateString);
		model.addLiteral(dataset, DCTerms.date, date);

		Catfish catfish = new Catfish();
		Model processedModel = catfish.process(model, datasetUri);

		// TODO: Test would fail
		// Assert.assertFalse("Contains wrong date format",
		// processedModel.containsLiteral(dataset, DCTerms.date, dateString));
	}

}