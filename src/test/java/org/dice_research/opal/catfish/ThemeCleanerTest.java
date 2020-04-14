package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.DCAT;
import org.dice_research.opal.catfish.service.impl.ThemeCleaner;
import org.junit.Assert;
import org.junit.Test;

public class ThemeCleanerTest {

    @Test
    public void givenModel_WhenContainsCorrectAndBlankThemes_ThenRemoveBlankThemes() {
        Model model = ModelFactory.createDefaultModel();
        model.read("org/dice_research/opal/catfish/dataSetWithCorrectAndBlankTheme.ttl");
        new ThemeCleaner().clean(model);
        Assert.assertEquals(1, model.listObjectsOfProperty(DCAT.theme).toList().size());
    }

}
