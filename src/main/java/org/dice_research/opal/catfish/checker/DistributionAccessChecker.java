package org.dice_research.opal.catfish.checker;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;

/**
 * Checks if a dataset has at least one distribution with access or download
 * information.
 *
 * @author Adrian Wilke
 */
public class DistributionAccessChecker {

	public boolean checkModel(Model model, String datasetUri) {
		Resource dataset = model.getResource(datasetUri);
		StmtIterator datasetStmtIterator = dataset.listProperties(DCAT.distribution);
		while (datasetStmtIterator.hasNext()) {
			RDFNode distributionRdfNode = datasetStmtIterator.next().getObject();
			if (distributionRdfNode.isResource()) {
				Resource distribution = distributionRdfNode.asResource();
				if (hasValidUri(distribution, DCAT.accessURL) || hasValidUri(distribution, DCAT.downloadURL)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasValidUri(Resource distribution, Property property) {
		StmtIterator stmtIterator = distribution.asResource().listProperties(property);
		while (stmtIterator.hasNext()) {
			try {
				new URI(stmtIterator.next().getObject().toString());
			} catch (URISyntaxException e) {
				// Ignore
			}
			return true;
		}
		return false;
	}

}