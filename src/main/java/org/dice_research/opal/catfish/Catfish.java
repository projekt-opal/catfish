package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dice_research.opal.common.interfaces.JenaModelProcessor;
import org.dice_research.opal.common.interfaces.ModelProcessor;

@SuppressWarnings("deprecation")
public class Catfish implements ModelProcessor, JenaModelProcessor {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {

		// TODO: clean model

		if (model.isEmpty()) {
			LOGGER.warn("Model is empty.");
		}

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