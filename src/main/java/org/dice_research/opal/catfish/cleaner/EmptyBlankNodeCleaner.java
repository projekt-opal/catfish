package org.dice_research.opal.catfish.cleaner;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * Removes blank nodes, which are not used as triple-subject.
 *
 * @author Adrian Wilke
 */
public class EmptyBlankNodeCleaner implements ModelProcessor {

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {
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