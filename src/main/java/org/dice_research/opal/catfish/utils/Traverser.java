package org.dice_research.opal.catfish.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Recursively traverses model and collects triples.
 *
 * @author Adrian Wilke
 */
public abstract class Traverser {

	/**
	 * Gets model with triples connected to resource.
	 */
	public static Model traverse(Model model, Resource resource) {
		Model removeModel = ModelFactory.createDefaultModel();
		traverse(model, resource, new HashSet<>(), removeModel);
		return removeModel;
	}

	private static void traverse(Model model, Resource resource, Set<String> processedResources,
			Model removeModel) {

		processedResources.add(resource.getURI());

		StmtIterator stmtIterator = model.listStatements(new ResourceSubjectSelector(resource));
		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();

			// Collect
			removeModel.add(stmt);

			// Recursively go on
			if (stmt.getObject().isResource()) {
				Resource object = stmt.getObject().asResource();

				// Process every resource only one time
				if (!processedResources.contains(object.getURI())) {
					traverse(model, object, processedResources, removeModel);
				}
			}
		}
	}
}