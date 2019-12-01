package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

public class DummyTest {

	/**
	 * Tests, if model is empty.
	 */
	@Test
	public void test() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		String datasetUri = "https://example.org/dataset1";

		new Catfish().processModel(model, datasetUri);

		Assert.assertEquals("Model is empty", 0, model.size());

		Assert.assertTrue("Model is empty", model.isEmpty());
	}

}