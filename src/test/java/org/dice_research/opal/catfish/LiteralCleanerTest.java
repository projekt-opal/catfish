package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

public class LiteralCleanerTest {

    @Test
    public void givenModel_WhenContainsCorrectAndBlankThemes_ThenRemoveBlankThemes() {
        Model model = ModelFactory.createDefaultModel();
        model.read("org/dice_research/opal/catfish/dataSetWithLanguagesAndDataTypedLiteral.nt");
        new LiteralCleaner().clean(model);
    }

}
