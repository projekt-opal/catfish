package org.dice_research.opal.catfish.service.impl;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;

/**
 * Selects all statements, having a subject or object equal to the selector
 * resource.
 * 
 * @author Adrian Wilke
 */
public class SubjectObjectSelector implements Selector {

	private Resource resource;

	public SubjectObjectSelector(Resource resource) {
		this.resource = resource;
	}

	@Override
	public boolean test(Statement s) {
		return (resource == null || resource.equals(s.getSubject())) || resource.equals(s.getObject());
	}

	@Override
	public boolean isSimple() {
		// Use test(...) instead of getSubject() getPredicate() getObject()
		return false;
	}

	@Override
	public Resource getSubject() {
		return null;
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