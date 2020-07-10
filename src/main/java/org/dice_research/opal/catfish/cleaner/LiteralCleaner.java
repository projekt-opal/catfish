package org.dice_research.opal.catfish.cleaner;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * Removes triples containing unspecified languages from model.
 * 
 * Removes empty literals.
 *
 * @author Adrian Wilke
 */
public class LiteralCleaner implements ModelProcessor {

	private boolean removeEmptyLiterals;
	private Set<String> whitelistExactLanguages;
	private Set<String> whitelistLanguagePrefixes;

	/**
	 * Removes all empty literals.
	 * 
	 * Does not remove literals with language tag 'de', 'en' and literals without
	 * language tag.
	 * 
	 * Does also not remove literals with prefixes 'de-' and 'en-'.
	 */
	public LiteralCleaner() {
		removeEmptyLiterals = true;

		whitelistExactLanguages = new HashSet<>();
		whitelistExactLanguages.add("de");
		whitelistExactLanguages.add("en");
		whitelistExactLanguages.add("");

		whitelistLanguagePrefixes = new HashSet<>();
		whitelistLanguagePrefixes.add("de-");
		whitelistLanguagePrefixes.add("en-");
	}

	public LiteralCleaner(boolean removeEmptyLiterals, Set<String> whitelistExactLanguages,
			Set<String> whitelistLanguagePrefixes) {

		if (whitelistExactLanguages == null || whitelistLanguagePrefixes == null) {
			throw new NullPointerException();
		}

		this.removeEmptyLiterals = removeEmptyLiterals;
		this.whitelistExactLanguages = whitelistExactLanguages;
		this.whitelistLanguagePrefixes = whitelistLanguagePrefixes;
	}

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {
		model.remove(model.listStatements(new Selector() {

			@Override
			public boolean test(Statement stmt) {

				// No literal: Do not remove
				if (!stmt.getObject().isLiteral()) {
					return false;
				}

				// Remove empty literals
				if (removeEmptyLiterals && stmt.getObject().asLiteral().getString().trim().isEmpty()) {
					return true;
				}

				String language = getLanguageOfLiteral(stmt.getObject());

				// Do not remove exact whitelist
				if (whitelistExactLanguages.contains(language)) {
					return false;
				}

				// Do not remove prefix whitelist
				for (String prefix : whitelistLanguagePrefixes) {
					if (language.startsWith(prefix)) {
						return false;
					}
				}

				// Other languages: remove
				return true;
			}

			@Override
			public boolean isSimple() {
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
		}));
	}

	public static String getLanguageOfLiteral(RDFNode literal) {
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