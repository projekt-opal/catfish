package org.dice_research.opal.catfish.cleaner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.utils.Traverser;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * Filters datasets by title languages.
 * 
 * To be included, a title has to be available in english and german. Language
 * tags have to be 'de' or 'en'. Or they have to start with 'de-' or 'en-'. This
 * includes translated titles of the EDP, e.g. 'de-t-fr-t0-mtec' or
 * 'en-t-fr-t0-mtec'.
 * 
 * Datasets not having both title-languages are removed from model.
 * 
 * Attention: This processor may remove the resource with datasetUri.
 *
 * @author Adrian Wilke
 */
public class TitleLanguageFilter implements ModelProcessor {

	public static final String DE = "de";
	public static final String DE_TRANSLATED = "de-";
	public static final String EN = "en";
	public static final String EN_TRANSLATED = "en-";

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {
		Resource dataset = model.getResource(datasetUri);

		if (!titleInEnglishAndGerman(dataset)) {

			// Remove dataset and related triples from model
			// Does not remove blank nodes
			model.remove(Traverser.traverse(model, dataset));

			// Remove everything, if no dataset left in model
			if (!model.listStatements(null, RDF.type, DCAT.Dataset).hasNext()
					&& !model.listSubjectsWithProperty(DCAT.dataset).hasNext()
					&& !model.listObjectsOfProperty(DCAT.dataset).hasNext()) {
				model.removeAll();
			}
		}
	}

	private boolean titleInEnglishAndGerman(Resource dataset) {
		boolean titleInEnglish = false;
		boolean titleInGerman = false;

		StmtIterator titleStmtIterator = dataset.listProperties(DCTerms.title);
		while (titleStmtIterator.hasNext()) {
			RDFNode title = titleStmtIterator.next().getObject();
			String language = LiteralCleaner.getLanguageOfLiteral(title);

			if (title.isLiteral()) {
				if (title.asLiteral().getString().trim().isEmpty()) {
					continue;
				}
			}

			if (isLanguageDe(language)) {
				titleInGerman = true;

			} else if (isLanguageEn(language)) {
				titleInEnglish = true;
			}
		}

		if (titleInEnglish && titleInGerman) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLanguageDe(String language) {
		if (language.equals(DE) || language.startsWith(DE_TRANSLATED)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLanguageEn(String language) {
		if (language.equals(EN) || language.startsWith(EN_TRANSLATED)) {
			return true;
		} else {
			return false;
		}
	}
}