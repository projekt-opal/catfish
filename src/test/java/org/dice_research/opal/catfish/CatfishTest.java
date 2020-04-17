package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.opal.catfish.utility.JenaModelUtilities;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CatfishTest {

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
        model.read("org/dice_research/opal/catfish/dataSetWithCorrectAndBlankTheme_LiteralWithLanguageAndDataType_EmptyLiteral.ttl");
        Model copyModel = JenaModelUtilities.getModelCopy(model);
        new Catfish(null).processModel(model, null);
        Assert.assertTrue("graph size must be less than the original graph",
                model.size() < copyModel.size());
    }

}