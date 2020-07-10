package org.dice_research.opal.catfish.cleaner;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.opal.common.interfaces.ModelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LiteralEncodingCleaner.
 *
 * @author mnafshin
 * 
 * @see https://github.com/projekt-opal/catfish/blob/1fa98182d03f016da47eff5e2e0fa30b57265dcc/src/main/java/org/dice_research/opal/catfish/LiteralCleaner.java
 */
public class LiteralEncodingCleaner implements ModelProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LiteralEncodingCleaner.class);

	private final List<LiteralByRegexCleaner> regexCleaners = Arrays.asList(new LanguageByRegexCleaner(),
			new DataTypeByRegexCleaner(), new NonReadableAndEmptyLiterByRegexCleaner());

	@Override
	public void processModel(Model model, String datasetUri) throws Exception {
		try {
			regexCleaners.forEach(byRegexCleaner -> {
				List<Statement> statements = model.listStatements().toList();
				statements.forEach(statement -> byRegexCleaner.cleanByRegex(model, statement));
			});
		} catch (Exception e) {
			logger.error("Error in cleaning literals", e);
		}
	}
}

abstract class LiteralByRegexCleaner {

	private static final Logger logger = LoggerFactory.getLogger(LiteralByRegexCleaner.class);
	private final String regex;

	protected LiteralByRegexCleaner(String regex) {
		this.regex = regex;
	}

	public void cleanByRegex(Model model, Statement statement) {
		try {
			RDFNode object = statement.getObject();
			if (object.isLiteral()) {
				Literal literal = object.asLiteral();
				String value = literal.getString().trim();
				Pattern pattern = Pattern.compile(regex);
				Matcher m = pattern.matcher(value);
				if (m.find()) {
					model.remove(statement);
					if (m.groupCount() > 1) {
						Literal modelLiteral = createNewLiteral(model, m.group(1), m.group(2));
						model.add(statement.getSubject(), statement.getPredicate(), modelLiteral);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in cleaning by regex", e);
		}

	}

	abstract Literal createNewLiteral(Model model, String g1, String g2);
}

class LanguageByRegexCleaner extends LiteralByRegexCleaner {

	/**
	 * It will select strings that have @ and 2 characters in the end of the string
	 * description of regex from regex101.com: * " matches the character " literally
	 * (case sensitive) * 1st Capturing Group (.*) * .* matches any character
	 * (except for line terminators) * * Quantifier — Matches between zero and
	 * unlimited times, as many times as possible, giving back as needed (greedy) *
	 * "@ matches the characters "@ literally (case sensitive) * 2nd Capturing Group
	 * (..) * . matches any character (except for line terminators) * . matches any
	 * character (except for line terminators)
	 */
	LanguageByRegexCleaner() {
		super("\"(.*)\"@(..)");
	}

	@Override
	Literal createNewLiteral(Model model, String g1, String g2) {
		return model.createLiteral(g1, g2);
	}
}

class DataTypeByRegexCleaner extends LiteralByRegexCleaner {

	/**
	 * It will select string that has a dcat datatype in the end which started with
	 * ^^ then has a prefix and then : in between, for something like ^^xsd:string
	 * or ^^http://www.w3.org/2001/XMLSchema#string description of regex from
	 * regex101.com: * " matches the character " literally (case sensitive) * 1st
	 * Capturing Group (.*) * .* matches any character (except for line terminators)
	 * * * Quantifier — Matches between zero and unlimited times, as many times as
	 * possible, giving back as needed (greedy) * " matches the character "
	 * literally (case sensitive) * \^ matches the character ^ literally (case
	 * sensitive) * \^ matches the character ^ literally (case sensitive) * 2nd
	 * Capturing Group (.*:.*) * .* matches any character (except for line
	 * terminators) * * Quantifier — Matches between zero and unlimited times, as
	 * many times as possible, giving back as needed (greedy) * : matches the
	 * character : literally (case sensitive) * .* matches any character (except for
	 * line terminators)
	 */
	DataTypeByRegexCleaner() {
		super("\"(.*)\"\\^\\^(.*:.*)");
	}

	@Override
	Literal createNewLiteral(Model model, String g1, String g2) {
		return model.createTypedLiteral(g1, g2);
	}
}

class NonReadableAndEmptyLiterByRegexCleaner extends LiteralByRegexCleaner {

	/**
	 * It checks any string that does not have any character or number. Example:
	 * "@#$%" or empty string description of regex from regex101.com: * ^ asserts
	 * position at start of a line * Match a single character not present in the
	 * list below [^a-zA-Z0-9]* * * Quantifier — Matches between zero and unlimited
	 * times, as many times as possible, giving back as needed (greedy) * a-z a
	 * single character in the range between a (index 97) and z (index 122) (case
	 * sensitive) * A-Z a single character in the range between A (index 65) and Z
	 * (index 90) (case sensitive) * 0-9 a single character in the range between 0
	 * (index 48) and 9 (index 57) (case sensitive) * $ asserts position at the end
	 * of a line
	 */
	protected NonReadableAndEmptyLiterByRegexCleaner() {
		super("^[^a-zA-Z0-9]*$");
	}

	@Override
	Literal createNewLiteral(Model model, String g1, String g2) {
		return null;
	}
}