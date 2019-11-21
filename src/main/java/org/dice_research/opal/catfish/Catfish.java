package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
// import org.apache.jena.rdf.model.ModelFactory;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import org.dice_research.opal.common.interfaces.JenaModelProcessor;

public class Catfish implements JenaModelProcessor {

	// private static final Logger LOGGER = LogManager.getLogger();

	public Model process(Model model, String datasetUri) throws Exception {

		// LOGGER.info("Processing dataset " + datasetUri);

		// Model returnModel = ModelFactory.createDefaultModel();
		// returnModel.add(model);
		// return returnModel;

		return model;
	}

}