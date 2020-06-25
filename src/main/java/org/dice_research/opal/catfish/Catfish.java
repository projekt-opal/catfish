package org.dice_research.opal.catfish;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.catfish.config.CleaningConfig;
import org.dice_research.opal.catfish.service.Cleanable;
import org.dice_research.opal.catfish.service.impl.DateFormatEqualizer;
import org.dice_research.opal.catfish.service.impl.EmptyBlankNodeCleaner;
import org.dice_research.opal.catfish.service.impl.FormatCleaner;
import org.dice_research.opal.catfish.service.impl.LiteralCleaner;
import org.dice_research.opal.common.interfaces.JenaModelProcessor;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * OPAL RDF and DCAT cleaning component.
 *
 * @author Adrian Wilke
 */
@SuppressWarnings("deprecation")
public class Catfish implements ModelProcessor, JenaModelProcessor {

	private final CleaningConfig cleaningConfig;

	public Catfish(CleaningConfig cleaningConfig) {
		this.cleaningConfig = cleaningConfig == null ? new CleaningConfig() : cleaningConfig;
	}

	private List<Cleanable> getCleaners() {
		List<Cleanable> ret = new ArrayList<>();

		if (cleaningConfig.isCleanEmptyBlankNodes())
			ret.add(new EmptyBlankNodeCleaner());

		if (cleaningConfig.isCleanFormats())
			ret.add(new FormatCleaner());

		if (cleaningConfig.isCleanLiterals())
			ret.add(new LiteralCleaner());

		if (cleaningConfig.isEqualizingDateFormats())
			ret.add(new DateFormatEqualizer());

		return ret;
	}

	/**
	 * @param model      Apache Jena Model that needs to be cleaned
	 * @param datasetUri unused
	 */
	@Override

	public void processModel(Model model, String datasetUri) {
		List<Cleanable> cleaners = getCleaners();
		cleaners.forEach(cleaner -> cleaner.clean(model));
	}

	/**
	 * @deprecated Replaced by {@link #processModel(Model, String)}.
	 */
	@Deprecated
	@Override
	public Model process(Model model, String datasetUri) {
		processModel(model, datasetUri);
		return model;
	}

}