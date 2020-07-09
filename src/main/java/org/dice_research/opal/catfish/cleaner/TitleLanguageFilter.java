package org.dice_research.opal.catfish.cleaner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.ResourceSubjectSelector;
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
 * Non-de-en titles and descriptions are removed by default.
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

		if (titleInEnglishAndGerman(dataset)) {
			// Remove non-de-en titles and descriptions

			List<Statement> stmtsToRemove = new LinkedList<>();

			// Collect titles
			StmtIterator titleStmtIterator = dataset.listProperties(DCTerms.title);
			while (titleStmtIterator.hasNext()) {
				Statement stmt = titleStmtIterator.next();
				if (!isLanguageDe(getLanguageOfLiteral(stmt.getObject()))
						&& !isLanguageEn(getLanguageOfLiteral(stmt.getObject()))) {
					stmtsToRemove.add(stmt);
				}
			}

			// Collect descriptions
			StmtIterator descriptionStmtIterator = dataset.listProperties(DCTerms.description);
			while (descriptionStmtIterator.hasNext()) {
				Statement stmt = descriptionStmtIterator.next();
				if (!isLanguageDe(getLanguageOfLiteral(stmt.getObject()))
						&& !isLanguageEn(getLanguageOfLiteral(stmt.getObject()))) {
					stmtsToRemove.add(stmt);
				}
			}

			// Remove
			model.remove(stmtsToRemove);

		} else {

			// Remove dataset and related triples from model
			// Does not remove blank nodes
			Model removeModel = ModelFactory.createDefaultModel();
			getDatasetStatements(model, dataset, new HashSet<>(), removeModel);
			model.remove(removeModel);

			// Remove everything, if no dataset left in model
			if (!model.listStatements(null, RDF.type, DCAT.Dataset).hasNext()
					&& !model.listSubjectsWithProperty(DCAT.dataset).hasNext()
					&& !model.listObjectsOfProperty(DCAT.dataset).hasNext()) {
				model.removeAll();
			}
		}
	}

	private void getDatasetStatements(Model model, Resource resource, Set<String> processedResources,
			Model removeModel) {
		processedResources.add(resource.getURI());

		StmtIterator stmtIterator = model.listStatements(new ResourceSubjectSelector(resource));
		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();

			// Collect
			removeModel.add(stmt);

			// Recursively go on
			if (stmt.getObject().isResource()) {
				Resource object = stmt.getObject().asResource();

				// Process every resource only one time
				if (!processedResources.contains(object.getURI())) {
					getDatasetStatements(model, object, processedResources, removeModel);
				}
			}
		}
	}

	private boolean titleInEnglishAndGerman(Resource dataset) {
		boolean titleInEnglish = false;
		boolean titleInGerman = false;

		StmtIterator titleStmtIterator = dataset.listProperties(DCTerms.title);
		while (titleStmtIterator.hasNext()) {
			RDFNode title = titleStmtIterator.next().getObject();
			String language = getLanguageOfLiteral(title);

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

	private String getLanguageOfLiteral(RDFNode literal) {
		String language = "";

		if (literal.isLiteral()) {
			language = literal.asLiteral().getLanguage();

			// Workaround for wrong parsing (occurred while reading nt file)
			if (language.trim().isEmpty()) {
				int index = literal.toString().lastIndexOf("\"@");
				if (index != -1) {

					// Get language
					language = literal.toString().substring(index + 2);

					// Avoid language tag inside text
					if (language.contains("\"")) {
						language = "";
					}
				}
			}
		}

		return language;
	}
}