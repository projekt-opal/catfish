package org.dice_research.opal.catfish.service.impl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_research.opal.catfish.service.Cleanable;

import java.util.LinkedList;
import java.util.List;

/**
 * Cleans structural contents, e.g. empty values.
 *
 * @author Adrian Wilke
 */
public class EmptyBlankNodeCleaner implements Cleanable {

    @Override
    public void clean(Model model) {
        StmtIterator stmtIterator = model.listStatements();
        List<Statement> statementsToRemove = new LinkedList<>();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            // Collect empty blank nodes
            if (statement.getObject().isAnon()) {
                if (!statement.getObject().asResource().listProperties().hasNext()) {
                    statementsToRemove.add(statement);
                }
            }
        }

        // Remove from model
        model.remove(statementsToRemove);
    }
}