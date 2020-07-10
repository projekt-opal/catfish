package org.dice_research.opal.catfish.utils;


import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;

/**
 * Selects triples containing resource as subject.
 *
 * @author Adrian Wilke
 */
public class ResourceSubjectSelector implements Selector {

	private Resource resource;

	public ResourceSubjectSelector(Resource resource) {
		this.resource = resource;
	}

	@Override
	public boolean test(Statement t) {
		return false;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Resource getSubject() {
		return resource;
	}

	@Override
	public Property getPredicate() {
		return null;
	}

	@Override
	public RDFNode getObject() {
		return null;
	}

}