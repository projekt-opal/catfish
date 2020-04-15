package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VCARD4;
import org.dice_research.opal.catfish.service.impl.LiteralCleaner;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class LiteralCleanerTest {

    @Test
    public void givenModel_WhenContainsLanguageAndDataTypedLiterals_ThenCleanTheStructure() {
        Model model = ModelFactory.createDefaultModel();
        model.read("org/dice_research/opal/catfish/dataSetWithLanguagesAndDataTypedLiteral.nt");
        new LiteralCleaner().clean(model);
        List<Statement> statements = model.listStatements().toList();
        statements.forEach(statement -> {
            RDFNode object = statement.getObject();
            if (!object.isLiteral() || statement.getPredicate().equals(VCARD4.hasEmail)) return;
            String literalValue = object.asLiteral().getString();
            Assert.assertFalse(literalValue.contains("@"));
            Assert.assertFalse(literalValue.contains("^^"));
        });
    }

    @Test
    public void givenModel_WhenContainsEmptyLiterals_ThenRemovesThem() throws IOException {
        TestCase testCase = OpalTestCases.getTestCase("edp-2019-12-17", "med-kodierungshandbuch");

        Model testModel = JenaModelUtilities.getModelCopy(testCase.getModel());
        new LiteralCleaner().clean(testModel);
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
