package org.dice_research.opal.catfish.cleaner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * Rewrites data format of datasets/distributions and modified/issued.
 * 
 * XSDDateType is replaced by XSDdateTime.
 * 
 * Note: This can be used if the format of dates has to be equal. Semantically,
 * it is not correct, as the time of dates is not known.
 * 
 * @author Adrian Wilke
 */
public class DateFormatEqualizer implements ModelProcessor {

	/**
	 * Processes date formats of datasets and distributions.
	 */
	@Override
	public void processModel(Model model, String datasetUri) throws Exception {

		ResIterator resIterator;
		Resource resource;

		// Datasets

		resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
		while (resIterator.hasNext()) {
			resource = resIterator.next();
			clean(model, resource, DCTerms.modified);
			clean(model, resource, DCTerms.issued);
		}

		// Distributions

		resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Distribution);
		while (resIterator.hasNext()) {
			resource = resIterator.next();
			clean(model, resource, DCTerms.modified);
			clean(model, resource, DCTerms.issued);
		}
	}

	/**
	 * Processes all objects related to the given subject and predicate.
	 */
	private void clean(Model model, Resource s, Property p) {
		Map<RDFNode, Literal> replacements = new HashMap<>();
		StmtIterator stmtIterator = s.listProperties(p);
		while (stmtIterator.hasNext()) {
			RDFNode rdfNode = stmtIterator.next().getObject();
			Literal replacement = getObjectRelacement(model, s, p, rdfNode);
			if (replacement != null) {
				replacements.put(rdfNode, replacement);
			}
		}
		for (Entry<RDFNode, Literal> entry : replacements.entrySet()) {
			model.add(s, p, entry.getValue());
			model.remove(s, p, entry.getKey());
		}
	}

	/**
	 * Returns object to replace or null.
	 */
	private Literal getObjectRelacement(Model model, Resource s, Property p, RDFNode o) {
		// How to create literals:
		// https://franz.com/agraph/support/documentation/current/java-tutorial/jena-tutorial.html#Literal%20Values
		if (o.isLiteral() && o.asLiteral().getDatatype() instanceof XSDDateType) {
			return model.createTypedLiteral(o.asLiteral().getString() + "T00:00:00", XSDDatatype.XSDdateTime)
					.asLiteral();
		}
		return null;
	}
}