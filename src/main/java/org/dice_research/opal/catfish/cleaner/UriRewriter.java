package org.dice_research.opal.catfish.cleaner;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.constants.Catalogs;
import org.dice_research.opal.common.interfaces.ModelProcessor;
import org.dice_research.opal.common.vocabulary.Opal;

/**
 * Creates new URIs for datasets and distributions.
 * 
 * Additionally, the old URIs are linked by {@link Opal#originalUri}.
 * 
 * @author Adrian Wilke
 */
public class UriRewriter implements ModelProcessor {

	private String catalog;
	private String newDatasetUri;

	/**
	 * Creates new rewriter for given catalog.
	 * 
	 * @param catalog A value specified in {@link Catalogs}.
	 * @throws RuntimeException If catalog is null or blank.
	 */
	public UriRewriter(String catalog) throws RuntimeException {
		if (catalog == null || catalog.trim().isEmpty()) {
			throw new RuntimeException("No catalog given");
		}
		this.catalog = catalog;
	}

	/**
	 * Replaces all DCAT.Dataset and DCAT.Distribution resources with new ones based
	 * on the given catalog.
	 */
	@Override
	public void processModel(Model model, String datasetUri) throws Exception {
		for (Resource dataset : collectResourcesOfType(model, DCAT.Dataset)) {
			Resource newDataset = replaceAll(model, dataset, DCAT.Dataset);

			// Set the new dataset URI
			if (dataset.getURI().equals(datasetUri)) {
				newDatasetUri = newDataset.getURI();
			}
		}

		for (Resource distribution : collectResourcesOfType(model, DCAT.Distribution)) {
			replaceAll(model, distribution, DCAT.Distribution);
		}
	}

	/**
	 * Gets all resources of given type.
	 */
	private List<Resource> collectResourcesOfType(Model model, Resource resourceType) {
		List<Resource> resources = new LinkedList<>();
		StmtIterator stmtIterator = model.listStatements(null, RDF.type, resourceType);
		while (stmtIterator.hasNext()) {
			resources.add(stmtIterator.next().getSubject());
		}
		return resources;
	}

	/**
	 * Replaces resources.
	 */
	private Resource replaceAll(Model model, Resource resource, Resource resourceType) {
		Resource newResource = getNewResource(resource.getURI(), resourceType.getURI());

		// Collect statements
		List<Statement> stmts = new LinkedList<>();
		StmtIterator stmtIterator = model.listStatements(new SubjectObjectSelector(resource));
		while (stmtIterator.hasNext()) {
			stmts.add(stmtIterator.next());
		}

		// Add new resource and remove old resource
		for (Statement stmt : stmts) {
			if (stmt.getSubject().equals(resource) && stmt.getObject().equals(resource)) {
				model.add(newResource, stmt.getPredicate(), newResource);
				model.remove(resource, stmt.getPredicate(), resource);
			}

			else if (stmt.getSubject().equals(resource)) {
				model.add(newResource, stmt.getPredicate(), stmt.getObject());
				model.remove(resource, stmt.getPredicate(), stmt.getObject());
			}

			else if (stmt.getObject().equals(resource)) {
				model.add(stmt.getSubject(), stmt.getPredicate(), newResource);
				model.remove(stmt.getSubject(), stmt.getPredicate(), resource);
			}
		}

		// Add reference to old resource
		model.add(newResource, Opal.PROP_ORIGINAL_URI, resource);

		return newResource;
	}

	/**
	 * Creates new resource for the given input.
	 * 
	 * @param uriToReplace Current URI of dataset or distribution.
	 * @param type         URI of DCAT.Dataset or DCAT.Distribution.
	 */
	private Resource getNewResource(String uriToReplace, String resourceTypeUri) {
		String newUri;

		if (resourceTypeUri.equals(DCAT.Dataset.getURI())) {
			newUri = org.dice_research.opal.common.utilities.UriRewriter.getOpalDatasetUri(catalog, uriToReplace);

		} else if (resourceTypeUri.equals(DCAT.Distribution.getURI())) {
			newUri = org.dice_research.opal.common.utilities.UriRewriter.getOpalDistributionUri(catalog, uriToReplace);

		} else {
			throw new RuntimeException("Unknown type: " + resourceTypeUri);
		}

		return ResourceFactory.createResource(newUri);
	}

	/**
	 * Gets the replaced URI for the given datasetUri.
	 */
	public String getNewDatasetUri() {
		return newDatasetUri;
	}
}