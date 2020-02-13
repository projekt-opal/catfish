package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.common.interfaces.JenaModelProcessor;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * OPAL RDF and DCAT cleaning component.
 * 
 * @author Adrian Wilke
 */
@SuppressWarnings("deprecation")
public class Catfish implements ModelProcessor, JenaModelProcessor {

	private boolean removeEmptyBlankNodes = true;
	private boolean removeEmptyLiterals = true;

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {

		// Clean structural contents, e.g. empty values
		new StructuralCleaner(this).clean(model, datasetUri);
	}

	public boolean isRemovingEmptyBlankNodes() {
		return removeEmptyBlankNodes;
	}

	public boolean isRemovingEmptyLiterals() {
		return removeEmptyLiterals;
	}

	public Catfish removeEmptyBlankNodes(boolean removeEmptyBlankNodes) {
		this.removeEmptyBlankNodes = removeEmptyBlankNodes;
		return this;
	}

	public Catfish removeEmptyLiterals(boolean removeEmptyLiterals) {
		this.removeEmptyLiterals = removeEmptyLiterals;
		return this;
	}

	/**
	 * @deprecated Replaced by {@link #processModel(Model, String)}.
	 */
	@Deprecated
	@Override
	public Model process(Model model, String datasetUri) throws Exception {
		processModel(model, datasetUri);
		return model;
	}
}