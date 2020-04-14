package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiteralCleaner implements Cleaner {

    private static final Logger logger = LoggerFactory.getLogger(LiteralCleaner.class);

    private final List<LiteralByRegexCleaner> regexCleaners =
            Arrays.asList(new LanguageByRegexCleaner(), new DataTypeByRegexCleaner());

    @Override
    public void clean(Model model) {
        try {
            List<Statement> statements = model.listStatements().toList();
            statements.forEach(statement ->
                    regexCleaners.forEach(byRegexCleaner -> byRegexCleaner.cleanByRegex(model, statement)));
        } catch (Exception e) {
            logger.error("Error in cleaning literals", e);
        }
    }
}


abstract class LiteralByRegexCleaner {

    private static final Logger logger = LoggerFactory.getLogger(LiteralByRegexCleaner.class);
    private final String regex;

    protected LiteralByRegexCleaner(String regex) {
        this.regex = regex;
    }

    public void cleanByRegex(Model model, Statement statement) {
        try {
            RDFNode object = statement.getObject();
            if (object.isLiteral()) {
                Literal literal = object.asLiteral();
                String value = literal.getString();
                //            if (value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') return;
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(value);
                if (m.find()) {
                    Literal modelLiteral = createNewLiteral(model, m.group(1), m.group(2));
                    model.remove(statement);
                    model.add(statement.getSubject(), statement.getPredicate(), modelLiteral);
                }
            }
        } catch (Exception e) {
            logger.error("Error in cleaning by regex", e);
        }

    }

    abstract Literal createNewLiteral(Model model, String g1, String g2);
}

class LanguageByRegexCleaner extends LiteralByRegexCleaner {

    LanguageByRegexCleaner() {
        super("\"(.*)\"@(..)");
    }

    @Override
    Literal createNewLiteral(Model model, String g1, String g2) {
        return model.createLiteral(g1, g2);
    }
}

class DataTypeByRegexCleaner extends LiteralByRegexCleaner {

    DataTypeByRegexCleaner() {
        super("\"(.*)\"\\^\\^(.*:.*)");
    }

    @Override
    Literal createNewLiteral(Model model, String g1, String g2) {
        return model.createTypedLiteral(g1, g2);
    }
}