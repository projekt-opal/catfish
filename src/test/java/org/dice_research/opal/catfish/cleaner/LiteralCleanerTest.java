package org.dice_research.opal.catfish.cleaner;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VCARD4;
import org.dice_research.opal.catfish.cleaner.LiteralCleaner;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class LiteralCleanerTest {

	@Test
	public void givenModel_WhenContainsLanguageAndDataTypedLiterals_ThenCleanTheStructure() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		model.read("org/dice_research/opal/catfish/dataSetWithLanguagesAndDataTypedLiteral.nt");
		new LiteralCleaner().processModel(model, null);
		List<Statement> statements = model.listStatements().toList();
		statements.forEach(statement -> {
			RDFNode object = statement.getObject();
			if (!object.isLiteral() || statement.getPredicate().equals(VCARD4.hasEmail))
				return;
			String literalValue = object.asLiteral().getString();
			Assert.assertFalse(literalValue.contains("@"));
			Assert.assertFalse(literalValue.contains("^^"));
		});
	}

	@Test
	public void givenModel_WhenContainsEmptyLiterals_ThenRemovesThem() throws Exception {
		TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");

		Model testModel = JenaModelUtilities.getModelCopy(testCase.getModel());
		new LiteralCleaner().processModel(testModel, null);
		Assert.assertTrue("StructuralCleaner removes empty literal", testModel.size() < testCase.getModel().size());

		Resource dataset = testModel.getResource(testCase.getDatasetUri());
		Literal emptyEnLiteral = ResourceFactory.createLangLiteral("", "en");
		Assert.assertTrue("Empty title in source",
				testCase.getModel().contains(dataset, DCTerms.title, emptyEnLiteral));
		Assert.assertFalse("No empty title", testModel.contains(dataset, DCTerms.title, emptyEnLiteral));

		// Manual checks
		if (Boolean.FALSE) {
			System.out.println("Lit: Source " + testCase.getModel().size() + ", cleaned " + testModel.size());
		}
		if (Boolean.FALSE) {
			testCase.getModel().write(System.out, "TURTLE");
			System.out.println("---");
			testModel.write(System.out, "TURTLE");
		}
	}

}
