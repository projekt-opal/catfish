package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.VCARD4;
import org.dice_research.opal.catfish.service.impl.LiteralCleaner;
import org.junit.Assert;
import org.junit.Test;

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

}
